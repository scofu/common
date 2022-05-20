package com.scofu.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

/**
 * Represents a base for things that can be expanded.
 *
 * @param <T> the type of the expandable
 */
public interface Expandable<T extends Expandable<T>> {

  /** Returns the expansions. */
  ExpansionMap expansions();

  /**
   * Returns the optional value of an optional expansion with the given identifier.
   *
   * @param identifier the identifier
   * @param <V> the type of the value
   */
  default <V> Optional<V> expand(Identifier<V> identifier) {
    checkNotNull(identifier, "identifier");
    //noinspection unchecked
    return Optional.ofNullable(expansions().get(identifier))
        .map(o -> (Expansion<V>) o)
        .flatMap(Expansion::get);
  }

  /**
   * Maps the given identifier to an expansion.
   *
   * @param identifier the identifier
   * @param <V> the type of the identifier
   */
  default <V> ExpansionBuilder<V, T> map(Identifier<V> identifier) {
    checkNotNull(identifier, "identifier");
    //noinspection unchecked
    return new ExpansionBuilder<>((T) this, expansion -> expansions().put(identifier, expansion));
  }
}
