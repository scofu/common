package com.scofu.common.inject;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.scofu.common.inject.internal.InternalListBinder;
import java.lang.annotation.Annotation;
import java.util.function.Function;

/**
 * Binds an ordered {@link java.util.List}{@code <T>}, and {@link java.util.List}{@code <}{@link
 * javax.inject.Provider}{@code <T>>}.
 *
 * @param <T> the type of the elements
 */
public interface ListBinder<T> {

  /**
   * Creates and returns a new list binder.
   *
   * @param binder      the binder
   * @param typeLiteral the type literal
   * @param <T>         the type of the elements in the list
   */
  static <T> ListBinder<T> newListBinder(Binder binder, TypeLiteral<T> typeLiteral) {
    return InternalListBinder.newInternalListBinder(
        binder.skipSources(ListBinder.class), typeLiteral);
  }

  /**
   * Creates and returns a new list binder.
   *
   * @param binder      the binder
   * @param typeLiteral the type literal
   * @param annotation  the annotation
   * @param <T>         the type of the elements in the list
   */
  static <T> ListBinder<T> newListBinder(
      Binder binder, TypeLiteral<T> typeLiteral, Annotation annotation) {
    return InternalListBinder.newInternalListBinder(
        binder.skipSources(ListBinder.class), typeLiteral, annotation);
  }

  /**
   * Creates and returns a new list binder.
   *
   * @param binder the binder
   * @param type   the type
   * @param <T>    the type of the elements in the list
   */
  static <T> ListBinder<T> newListBinder(Binder binder, Class<T> type) {
    return InternalListBinder.newInternalListBinder(
        binder.skipSources(ListBinder.class), TypeLiteral.get(type));
  }

  /**
   * Creates and returns a new list binder.
   *
   * @param binder     the binder
   * @param type       the type
   * @param annotation the annotation
   * @param <T>        the type of the elements in the list
   */
  static <T> ListBinder<T> newListBinder(Binder binder, Class<T> type, Annotation annotation) {
    return InternalListBinder.newInternalListBinder(
        binder.skipSources(ListBinder.class), TypeLiteral.get(type), annotation);
  }

  /**
   * Adds a binding.
   */
  LinkedBindingBuilder<T> addBinding();

  /**
   * Inserts a binding at a dynamic index determined by the given function.
   *
   * @param index the index
   */
  LinkedBindingBuilder<T> insertBinding(Function<Integer, Integer> index);

  /**
   * Inserts a binding at the beginning of the list.
   */
  LinkedBindingBuilder<T> insertFirstBinding();

  /**
   * Inserts a binding at the middle of the list.
   */
  LinkedBindingBuilder<T> insertMiddleBinding();

  /**
   * Inserts a binding at the end of the list.
   */
  LinkedBindingBuilder<T> insertLastBinding();
}
