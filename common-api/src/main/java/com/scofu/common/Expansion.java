package com.scofu.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Represents a dynamic expansion of an expandable object.
 *
 * <p>See {@link Expandable}.
 *
 * @param <T> the type of the value
 */
public interface Expansion<T> {

  /**
   * Returns an empty expansion.
   *
   * @param <T> the type of the value
   */
  static <T> Expansion<T> empty() {
    //noinspection unchecked
    return Empty.EMPTY;
  }

  /**
   * Creates and returns a new expansion with the given value.
   *
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> Expansion<T> value(T value) {
    checkNotNull(value, "value");
    return new Value<>(Optional.of(value));
  }

  /**
   * Creates and returns a new optional expansion with the given optonal value.
   *
   * @param value the value
   * @param <T> the type of the value
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  static <T> Expansion<T> optional(Optional<T> value) {
    checkNotNull(value, "value");
    return new Value<>(value);
  }

  /**
   * Creates and returns a new lazy expansion with the given supplier.
   *
   * @param supplier the vsupplieralue
   * @param <T> the type of the value
   */
  static <T> Expansion<T> lazy(Supplier<T> supplier) {
    checkNotNull(supplier, "supplier");
    return new Lazy<>(supplier);
  }

  /** Returns the optional value. */
  Optional<T> get();

  /** See {@link Optional#orElseThrow()}. */
  default T orElseThrow() {
    return get().orElseThrow();
  }

  /** Returns a new builder from this. */
  default ExpansionBuilder<T, Expansion<T>> toBuilder() {
    final var result = new AtomicReference<Expansion<T>>();
    return new ExpansionBuilder<>(this, result::get, result::set);
  }

  /** Empty expansion. */
  class Empty implements Expansion {

    private static final Empty EMPTY = new Empty();

    @Override
    public Optional get() {
      return Optional.empty();
    }
  }

  /**
   * Value expansion.
   *
   * @param <T> the type of the value
   */
  class Value<T> implements Expansion<T> {

    private final Optional<T> value;

    public Value(Optional<T> value) {
      this.value = value;
    }

    @Override
    public Optional<T> get() {
      return value;
    }
  }

  /**
   * Lazy expansion.
   *
   * @param <T> the type of the value
   */
  class Lazy<T> implements Expansion<T> {

    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public Optional<T> get() {
      return Optional.of(supplier.get());
    }
  }
}
