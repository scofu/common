package com.scofu.common.json.lazy;

import static com.google.common.base.Defaults.defaultValue;
import static com.jsoniter.any.Any.rewrap;
import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Optional.empty;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.internal.MoreTypes;
import com.jsoniter.ValueType;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.any.Any;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Json;
import com.scofu.common.json.lazy.InternalLazyFactory.Binding.Type;
import com.scofu.common.reflect.MethodRecorder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

final class InternalLazyFactory implements LazyFactory {

  private final Map<Method, Optional<Binding>> bindings;
  private final Json json;

  @Inject
  InternalLazyFactory(Json json) {
    this.json = json;
    this.bindings = Maps.newConcurrentMap();
  }

  @Override
  public <T extends Lazy> T create(Class<T> type, Any any) {
    final var lazy =
        newProxyInstance(getClass().getClassLoader(), new Class[] {type}, new Body(any, type));
    return type.cast(lazy);
  }

  @Override
  public <T extends Lazy> T create(Class<T> type, Map<Function<T, ?>, Object> args) {
    final var any = rewrap(Maps.newLinkedHashMap());
    if (args.isEmpty()) {
      return create(type, any);
    }
    try (final var recorder = MethodRecorder.record(type, args.keySet())) {
      final var iterator = args.values().iterator();
      for (var method : recorder.methods()) {
        if (!iterator.hasNext()) {
          throw new IllegalStateException(
              String.format("Expected argument for method %s.", method));
        }
        final var arg = iterator.next();
        bindings
            .computeIfAbsent(method, this::parseBinding)
            .ifPresent(
                binding -> {
                  if (binding.type != Type.GETTER && binding.type != Type.OPTIONAL_GETTER) {
                    throw new IllegalStateException(
                        String.format(
                            "Expected getter but got %s for method %s.", binding.type, method));
                  }
                  any.asMap().put(binding.key, new ForwardingAny(arg, binding.typeLiteral));
                });
      }
    }
    return create(type, any);
  }

  @Override
  public <T extends Lazy> T create(Class<T> type) {
    return create(type, Map.of());
  }

  private Optional<Binding> parseBinding(Method method) {
    if (Void.TYPE.isAssignableFrom(method.getReturnType())) {
      if (method.getParameterCount() == 1) {
        final var key =
            parsePrefixedKey(method, "setIs")
                .or(() -> parsePrefixedKey(method, "set"))
                .orElseGet(method::getName);
        return Optional.of(
            new Binding(
                key, TypeLiteral.create(method.getGenericParameterTypes()[0]), Type.SETTER));
      }
      return empty();
    }
    if (method.getParameterCount() != 0) {
      return empty();
    }
    return parseIncrementer(method)
        .or(() -> parseDecrementer(method))
        .or(() -> parseOptionalGetter(method));
  }

  private Optional<Binding> parseIncrementer(Method method) {
    return parsePrefixedKey(method, "increment")
        .map(
            key ->
                new Binding(
                    key, TypeLiteral.create(method.getGenericReturnType()), Type.INCREMENTER));
  }

  private Optional<Binding> parseDecrementer(Method method) {
    return parsePrefixedKey(method, "decrement")
        .map(
            key ->
                new Binding(
                    key, TypeLiteral.create(method.getGenericReturnType()), Type.DECREMENTER));
  }

  private Optional<Binding> parseOptionalGetter(Method method) {
    return parsePrefixedKey(method, "get")
        .or(() -> parsePrefixedKey(method, "is"))
        .or(() -> parseKey(method))
        .map(key -> parseOptionalGetter(method, key));
  }

  private Binding parseOptionalGetter(Method method, String key) {
    if (Optional.class.isAssignableFrom(method.getReturnType())) {
      final var type =
          method.getGenericReturnType() instanceof ParameterizedType parameterizedType
              ? parameterizedType.getActualTypeArguments()[0]
              : method.getGenericReturnType();
      return new Binding(key, TypeLiteral.create(type), Type.OPTIONAL_GETTER);
    }
    return new Binding(key, TypeLiteral.create(method.getGenericReturnType()), Type.GETTER);
  }

  private Optional<String> parseKey(Method method) {
    return Optional.ofNullable(method.getAnnotation(JsonProperty.class))
        .map(JsonProperty::value)
        .or(() -> Optional.of(method.getName()));
  }

  private Optional<String> parsePrefixedKey(Method method, String prefix) {
    final var name = parseKey(method).orElseThrow();
    // ...
    if (!name.startsWith(prefix)) {
      return empty();
    }
    if (name.length() == prefix.length() + 1) {
      // set<...>
      final var character = name.substring(prefix.length()).charAt(0);
      if (!Character.isUpperCase(character)) {
        // seta
        // a
        return empty();
      }
      // setA
      // A
      // a
      return Optional.of(String.valueOf(Character.toLowerCase(character)));
    }
    // setAnimal
    // nimal
    final var substring = name.substring(prefix.length() + 1);
    // a + nimal
    return Optional.of(Character.toLowerCase(name.charAt(prefix.length())) + substring);
  }

  record Binding(String key, TypeLiteral<?> typeLiteral, Type type) {

    public Optional<Object> invoke(Any any, Object[] args) {
      switch (type) {
        case GETTER -> {
          return Optional.ofNullable(any.asMap().get(key))
              .map(serialized -> read(serialized, any, key));
        }
        case OPTIONAL_GETTER -> {
          return Optional.of(
              Optional.ofNullable(any.asMap().get(key))
                  .map(serialized -> read(serialized, any, key)));
        }
        case SETTER -> {
          if (args[0] == null) {
            any.asMap().remove(key);
            return Optional.empty();
          }
          any.asMap().put(key, new ForwardingAny(args[0], typeLiteral));
          return Optional.empty();
        }
        case INCREMENTER -> {
          final var rawType = MoreTypes.getRawType(typeLiteral.getType());
          final var valueAny = any.asMap().getOrDefault(key, Any.wrapNull());
          final Object value;
          if (int.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toInt() + 1 : 1;
          } else if (long.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toLong() + 1L : 1L;
          } else if (float.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toFloat() + 1F : 1F;
          } else if (double.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toDouble() + 1D : 1D;
          } else if (BigInteger.class.isAssignableFrom(rawType)) {
            value =
                valueAny.valueType() == ValueType.NUMBER
                    ? valueAny.toBigInteger().add(BigInteger.ONE)
                    : BigInteger.ONE;
          } else if (BigDecimal.class.isAssignableFrom(rawType)) {
            value =
                valueAny.valueType() == ValueType.NUMBER
                    ? valueAny.toBigDecimal().add(BigDecimal.ONE)
                    : BigDecimal.ONE;
          } else {
            throw new UnsupportedOperationException("Not a number: " + this);
          }
          any.asMap().put(key, new ForwardingAny(value, typeLiteral));
          return Optional.of(value);
        }
        case DECREMENTER -> {
          final var rawType = MoreTypes.getRawType(typeLiteral.getType());
          final var valueAny = any.asMap().getOrDefault(key, Any.wrapNull());
          final Object value;
          if (int.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toInt() - 1 : -1;
          } else if (long.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toLong() - 1L : -1L;
          } else if (float.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toFloat() - 1F : -1F;
          } else if (double.class.isAssignableFrom(rawType)) {
            value = valueAny.valueType() == ValueType.NUMBER ? valueAny.toDouble() - 1D : -1D;
          } else if (BigInteger.class.isAssignableFrom(rawType)) {
            value =
                valueAny.valueType() == ValueType.NUMBER
                    ? valueAny.toBigInteger().subtract(BigInteger.ONE)
                    : BigInteger.valueOf(-1L);
          } else if (BigDecimal.class.isAssignableFrom(rawType)) {
            value =
                valueAny.valueType() == ValueType.NUMBER
                    ? valueAny.toBigDecimal().subtract(BigDecimal.ONE)
                    : BigDecimal.valueOf(-1L);
          } else {
            throw new UnsupportedOperationException("Not a number: " + this);
          }
          any.asMap().put(key, new ForwardingAny(value, typeLiteral));
          return Optional.of(value);
        }
        default -> throw new UnsupportedOperationException();
      }
    }

    private Object read(Any any, Any parent, String key) {
      if (any instanceof ForwardingAny forwardingAny) {
        return forwardingAny.object();
      }
      final var deserialized = any.as(typeLiteral);
      parent.asMap().put(key, new ForwardingAny(deserialized, typeLiteral));
      return deserialized;
    }

    enum Type {
      GETTER,
      OPTIONAL_GETTER,
      SETTER,
      INCREMENTER,
      DECREMENTER
    }
  }

  final class Body implements InvocationHandler, Lazy {

    private final Any any;
    private final Class<?> realClass;

    private Body(Any any, Class<?> realClass) {
      this.any = any;
      this.realClass = realClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.isDefault()) {
        return InvocationHandler.invokeDefault(proxy, method, args);
      } else if (method.getDeclaringClass().isAssignableFrom(getClass())) {
        return method.invoke(this, args);
      } else {
        return bindings
            .computeIfAbsent(method, InternalLazyFactory.this::parseBinding)
            .flatMap(binding -> binding.invoke(any, args))
            .orElseGet(() -> defaultValue(method.getReturnType()));
      }
    }

    @Override
    public Any any() {
      return any;
    }

    @Override
    public Class<?> realClass() {
      return realClass;
    }

    @Override
    public int hashCode() {
      return any.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      } else if (obj instanceof Lazy lazy) {
        return Objects.equals(any.hashCode(), lazy.any().hashCode());
      }
      return false;
    }

    @Override
    public String toString() {
      return json.toString(Any.class, any);
    }
  }
}
