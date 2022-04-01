package com.scofu.common.inject.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Types;
import com.scofu.common.inject.ListBinder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Provider;

/**
 * Fairly hacky way to bind an ordered list. See {@link MapBinder}.
 *
 * <p>Creates bindings for <pre>{@code List<T>}</pre> and <pre>{@code List<Provider<T>>}</pre>
 * (with provided annotation), to
 * <pre>{@code Map<Integer, T>}</pre> and <pre>{@code Map<Integer, Provider<T>>}</pre>
 *
 * <p>Internally creates bindings (with internal annotation), for <pre>{@code Map<Function<Integer,
 * Integer>, Set<T>>}</pre> and <pre>{@code Map<Function<Integer, Integer, Set<Provider<T>>}</pre>
 *
 * <p>Where {@code Provider} is both {@link Provider} and {@link com.google.inject.Provider}.
 *
 * @param <T> the type of the elements
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class InternalListBinder<T> extends AbstractModule implements ListBinder<T> {

  private static final Map<Key, AtomicInteger> INDICES_BY_KEY = Maps.newConcurrentMap();

  @SuppressWarnings("Convert2Diamond")
  private static final TypeLiteral<Function<Integer, Integer>> INSERT_TYPE_LITERAL
      = new TypeLiteral<Function<Integer, Integer>>() {};

  private final TypeLiteral<T> typeLiteral;
  private final Annotation annotation;
  private final MapBinder<Integer, T> mapBinder;
  private final MapBinder<Function<Integer, Integer>, T> insertedMapBinder;
  private final AtomicInteger index;

  private InternalListBinder(Binder binder, TypeLiteral<T> typeLiteral, Annotation annotation) {
    this.typeLiteral = typeLiteral;
    this.annotation = annotation;
    this.mapBinder = createMapBinder(binder, typeLiteral);
    this.insertedMapBinder = createInsertedMapBinder(binder, typeLiteral);
    this.index = getIndex(binder, typeLiteral);
  }

  public static <T> InternalListBinder<T> newInternalListBinder(Binder binder,
      TypeLiteral<T> typeLiteral) {
    return new InternalListBinder<>(binder.skipSources(InternalListBinder.class), typeLiteral,
        null);
  }

  public static <T> InternalListBinder<T> newInternalListBinder(Binder binder,
      TypeLiteral<T> typeLiteral, Annotation annotation) {
    return new InternalListBinder<>(binder.skipSources(InternalListBinder.class), typeLiteral,
        annotation);
  }

  @Override
  public LinkedBindingBuilder<T> addBinding() {
    return mapBinder.addBinding(index.getAndIncrement());
  }

  @Override
  public LinkedBindingBuilder<T> insertBinding(Function<Integer, Integer> index) {
    return insertedMapBinder.addBinding(index);
  }

  @Override
  public LinkedBindingBuilder<T> insertFirstBinding() {
    return insertBinding(size -> 0);
  }

  @Override
  public LinkedBindingBuilder<T> insertMiddleBinding() {
    return insertBinding(size -> size > 1 ? size / 2 : 0);
  }

  @Override
  public LinkedBindingBuilder<T> insertLastBinding() {
    return insertBinding(Function.identity());
  }

  @Override
  protected void configure() {
    // List<T>
    bind(newListKey()).toProvider(
            new ListProvider<>(getProvider(newMapKey()), getProvider(newInsertedMapKey())))
        .in(Scopes.SINGLETON);

    // List<javax Provider<T>>
    bind(newProviderListKey(Provider.class)).toProvider(
        new ListProvider<>(getProvider(newProviderMapKey(Provider.class)),
            getProvider(newProviderInsertedMapKey(Provider.class)))).in(Scopes.SINGLETON);

    // List<google Provider<T>>
    bind(newProviderListKey(com.google.inject.Provider.class)).toProvider(
            new ListProvider<>(getProvider(newProviderMapKey(com.google.inject.Provider.class)),
                getProvider(newProviderInsertedMapKey(com.google.inject.Provider.class))))
        .in(Scopes.SINGLETON);

    INDICES_BY_KEY.remove(
        annotation == null ? Key.get(typeLiteral) : Key.get(typeLiteral, annotation));
  }

  private <V> Key<V> getKey(Type type) {
    if (annotation == null) {
      return (Key<V>) Key.get(type);
    }
    return (Key<V>) Key.get(type, annotation);
  }

  private Key<Map<Integer, T>> newMapKey() {
    return this.getKey(Types.newParameterizedType(Map.class, Integer.class, typeLiteral.getType()));
  }

  private Key<Map<Function<Integer, Integer>, Set<T>>> newInsertedMapKey() {
    return (Key<Map<Function<Integer, Integer>, Set<T>>>) Key.get(
        Types.newParameterizedType(Map.class, INSERT_TYPE_LITERAL.getType(),
            Types.newParameterizedType(Set.class, typeLiteral.getType())),
        Internal.Access.get(annotation));
  }

  private Key<Map<Integer, Provider<T>>> newProviderMapKey(Class<? extends Provider> providerType) {
    return getKey(Types.newParameterizedType(Map.class, Integer.class,
        Types.newParameterizedType(providerType, typeLiteral.getType())));
  }

  private Key<Map<Function<Integer, Integer>, Set<Provider<T>>>> newProviderInsertedMapKey(
      Class<? extends Provider> providerType) {
    return (Key<Map<Function<Integer, Integer>, Set<Provider<T>>>>) Key.get(
        Types.newParameterizedType(Map.class, INSERT_TYPE_LITERAL.getType(),
            Types.newParameterizedType(Set.class,
                Types.newParameterizedType(providerType, typeLiteral.getType()))),
        Internal.Access.get(annotation));
  }

  private Key<List<T>> newListKey() {
    return getKey(Types.newParameterizedType(List.class, typeLiteral.getType()));
  }

  private Key<List<Provider<T>>> newProviderListKey(Class<? extends Provider> providerType) {
    return getKey(Types.newParameterizedType(List.class,
        Types.newParameterizedType(providerType, typeLiteral.getType())));
  }

  private MapBinder<Function<Integer, Integer>, T> createInsertedMapBinder(Binder binder,
      TypeLiteral<T> typeLiteral) {
    return MapBinder.newMapBinder(binder, INSERT_TYPE_LITERAL, typeLiteral,
        Internal.Access.get(annotation)).permitDuplicates();
  }

  private MapBinder<Integer, T> createMapBinder(Binder binder, TypeLiteral<T> typeLiteral) {
    if (annotation != null) {
      return MapBinder.newMapBinder(binder, TypeLiteral.get(Integer.class), typeLiteral,
          annotation);
    }
    return MapBinder.newMapBinder(binder, TypeLiteral.get(Integer.class), typeLiteral);
  }

  private AtomicInteger getIndex(Binder binder, TypeLiteral<T> typeLiteral) {
    synchronized (INDICES_BY_KEY) {
      final var key = annotation == null ? Key.get(typeLiteral) : Key.get(typeLiteral, annotation);
      var index = INDICES_BY_KEY.get(key);
      if (index == null) {
        binder.install(this);
        index = new AtomicInteger();
        INDICES_BY_KEY.put(key, index);
      }
      return index;
    }
  }

  private record ListProvider<T>(Provider<Map<Integer, T>> elements,
                                 Provider<?> provider) implements
      Provider<List<T>> {

    @Override
    public List<T> get() {
      final var list = elements.get().entrySet().stream()
          .sorted(Comparator.comparingInt(Entry::getKey)).map(Entry::getValue)
          .collect(Collectors.toCollection(Lists::newArrayList));
      ((Map<Function<Integer, Integer>, Set<T>>) provider.get()).forEach(
          (key, values) -> values.forEach(value -> list.add(key.apply(list.size()), value)));
      return list;
    }
  }
}
