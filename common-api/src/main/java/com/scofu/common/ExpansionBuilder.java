package com.scofu.common;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Binds expansions.
 *
 * @param <T> the type of the value
 * @param <S> the type of the parent
 */
public class ExpansionBuilder<T, S>
    extends AbstractBuilder<Expansion<T>, S, ExpansionBuilder<T, S>> {

  private Expansion<T> expansion;

  /**
   * Constructs a new expansion builder.
   *
   * @param from the from
   * @param parent the parent
   * @param consumer the consumer
   */
  public ExpansionBuilder(
      @Nullable Expansion<T> from, @Nullable S parent, @Nullable Consumer<Expansion<T>> consumer) {
    super(from, parent, consumer);
  }

  /**
   * Constructs a new expansion builder.
   *
   * @param parent the parent
   * @param consumer the consumer
   */
  public ExpansionBuilder(@Nullable S parent, @Nullable Consumer<Expansion<T>> consumer) {
    super(parent, consumer);
  }

  /**
   * Constructs a new expansion builder.
   *
   * @param from the from
   */
  public ExpansionBuilder(@Nullable Expansion<T> from) {
    super(from);
  }

  /** Constructs a new expansion builder. */
  public ExpansionBuilder() {}

  @Override
  protected void initializeFrom(Expansion<T> expansion) {
    this.expansion = expansion;
  }

  @Override
  public Expansion<T> build() {
    return require(expansion, "expansion");
  }

  /**
   * Binds the expansion to the given value.
   *
   * @param value the value
   */
  public S to(T value) {
    this.expansion = Expansion.value(value);
    return end();
  }

  /**
   * Binds the expansion to the given optional value.
   *
   * @param value the value
   */
  public S toOptional(Optional<T> value) {
    this.expansion = Expansion.optional(value);
    return end();
  }

  /** Binds the expansion to nothing. */
  public S toNothing() {
    this.expansion = Expansion.empty();
    return end();
  }

  /**
   * Binds the expansion to the given supplier.
   *
   * @param supplier the supplier
   */
  public S toSupplier(Supplier<T> supplier) {
    this.expansion = Expansion.lazy(supplier);
    return end();
  }
}
