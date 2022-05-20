package com.scofu.common;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;

/**
 * An abstract builder.
 *
 * @param <T> the type of the result
 * @param <S> the type of the parent
 * @param <U> the type of the builder
 */
public abstract class AbstractBuilder<T, S, U extends AbstractBuilder<T, S, U>>
    implements Builder<T, S, U> {

  private final S parent;
  private final Consumer<T> consumer;

  protected AbstractBuilder(@Nullable T from, @Nullable S parent, @Nullable Consumer<T> consumer) {
    if (parent != null) {
      checkNotNull(consumer, "consumer");
    } else {
      checkArgument(consumer == null, "consumer must also be null if parent is null");
    }
    this.parent = parent;
    this.consumer = consumer;
    if (from != null) {
      initializeFrom(from);
    }
  }

  protected AbstractBuilder(@Nullable S parent, @Nullable Consumer<T> consumer) {
    this(null, parent, consumer);
  }

  protected AbstractBuilder(@Nullable T from) {
    this(from, null, null);
  }

  protected AbstractBuilder() {
    this(null);
  }

  @Override
  public S end() {
    checkNotNull(parent, "parent");
    checkNotNull(consumer, "consumer");
    consumer.accept(build());
    return parent;
  }

  protected void initializeFrom(T t) {}

  protected <V> V require(V v, String name) {
    return checkNotNull(v, "missing required value for " + name);
  }

  protected <V> Optional<V> optional(@Nullable V v) {
    return Optional.ofNullable(v);
  }
}
