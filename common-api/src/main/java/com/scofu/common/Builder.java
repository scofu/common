package com.scofu.common;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A returnable builder.
 *
 * @param <T> the type of the result
 * @param <S> the type of the parent
 * @param <U> the type of the builder
 */
public interface Builder<T, S, U extends Builder<T, S, U>> {

  /** Returns the parent. */
  S end();

  /** Builds and returns a new result. */
  T build();

  /**
   * Builds and invokes the given consumer with a new result.
   *
   * @param consumer the consumer
   */
  default void build(Consumer<? super T> consumer) {
    consumer.accept(build());
  }

  /**
   * Returns the given operator applied to this builder.
   *
   * @param operator the operator
   */
  default U adopt(UnaryOperator<U> operator) {
    //noinspection unchecked
    return operator.apply((U) this);
  }
}
