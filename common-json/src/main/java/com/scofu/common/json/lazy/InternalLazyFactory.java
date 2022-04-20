package com.scofu.common.json.lazy;

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
    final var lazy = newProxyInstance(getClass().getClassLoader(), new Class[]{type},
        new Body(any));
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
        bindings.computeIfAbsent(method, this::parseBinding).ifPresent(binding -> {
          if (binding.type != Type.GETTER && binding.type != Type.OPTIONAL_GETTER) {
            throw new IllegalStateException(
                String.format("Expected getter but got %s for method %s.", binding.type, method));
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
        final var key = parseKey(method, "setIs").or(() -> parseKey(method, "set"))
            .orElseGet(method::getName);
        return Optional.of(
            new Binding(key, TypeLiteral.create(method.getGenericParameterTypes()[0]),
                Type.SETTER));
      }
      return empty();
    }
    if (method.getParameterCount() != 0) {
      return empty();
    }
    final var key = parseKey(method, "get").or(() -> parseKey(method, "is"))
        .orElseGet(method::getName);
    if (Optional.class.isAssignableFrom(method.getReturnType())) {
      final var type = method.getGenericReturnType() instanceof ParameterizedType parameterizedType
          ? parameterizedType.getActualTypeArguments()[0] : method.getGenericReturnType();
      return Optional.of(new Binding(key, TypeLiteral.create(type), Type.OPTIONAL_GETTER));
    }
    return Optional.of(
        new Binding(key, TypeLiteral.create(method.getGenericReturnType()), Type.GETTER));
  }

  private Optional<String> parseKey(Method method, String prefix) {
    final var annotation = method.getAnnotation(JsonProperty.class);
    if (annotation != null) {
      return Optional.of(annotation.value());
    }
    // ...
    if (!method.getName().startsWith(prefix)) {
      return empty();
    }
    if (method.getName().length() == prefix.length() + 1) {
      // set<...>
      final var character = method.getName().substring(prefix.length()).charAt(0);
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
    final var substring = method.getName().substring(prefix.length() + 1);
    // a + nimal
    return Optional.of(Character.toLowerCase(method.getName().charAt(prefix.length())) + substring);
  }

  record Binding(String key, TypeLiteral<?> typeLiteral, Type type) {

    public Optional<?> invoke(Any any, Object[] args) {
      switch (type) {
        case GETTER -> {
          return Optional.ofNullable(any.asMap().getOrDefault(key, Any.wrapNull()))
              .map(this::read);
        }
        case OPTIONAL_GETTER -> {
          return Optional.of(
              Optional.ofNullable(any.asMap().getOrDefault(key, Any.wrapNull()))
                  .map(this::read));
        }
        case SETTER -> {
          if (args[0] == null) {
            any.asMap().remove(key);
            return Optional.empty();
          }
          any.asMap().put(key, new ForwardingAny(args[0], typeLiteral));
          return Optional.empty();
        }
        default -> throw new UnsupportedOperationException();
      }
    }

    private Object read(Any any) {
      final var rawType = MoreTypes.getRawType(typeLiteral.getType());
      if (boolean.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.BOOLEAN && any.toBoolean();
      } else if (int.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toInt() : 0;
      } else if (long.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toLong() : 0L;
      } else if (float.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toFloat() : 0F;
      } else if (double.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toDouble() : 0D;
      } else if (BigInteger.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toBigInteger() : BigInteger.ZERO;
      } else if (BigDecimal.class.isAssignableFrom(rawType)) {
        return any.valueType() == ValueType.NUMBER ? any.toBigDecimal() : BigDecimal.ZERO;
      } else {
        return any.as(typeLiteral);
      }
    }

    enum Type {
      GETTER, OPTIONAL_GETTER, SETTER
    }
  }

  final class Body implements InvocationHandler, Lazy {

    private final Any any;

    private Body(Any any) {
      this.any = any;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (method.isDefault()) {
        return InvocationHandler.invokeDefault(proxy, method, args);
      } else if (method.getDeclaringClass().isAssignableFrom(getClass())) {
        return method.invoke(this, args);
      } else {
        return bindings.computeIfAbsent(method, InternalLazyFactory.this::parseBinding)
            .flatMap(binding -> binding.invoke(any, args)).orElse(null);
      }
    }

    @Override
    public Any any() {
      return any;
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
