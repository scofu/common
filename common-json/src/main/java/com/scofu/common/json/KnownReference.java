package com.scofu.common.json;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.scofu.common.json.internal.InternalKnownReference;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/**
 * Represents a known reference.
 *
 * <p>A known reference is ...
 *
 * @param <T> the type of the value
 */
public interface KnownReference<T> {

  /**
   * Creates and returns a known reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> KnownReference<T> known(@Nullable String type, @Nullable T value) {
    return InternalKnownReference.newInternalKnownReference(type, value);
  }

  /**
   * Creates and returns a known reference from the value.
   *
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> KnownReference<T> known(@Nullable T value) {
    return known(value == null ? null : value.getClass(), value);
  }

  /**
   * Creates and returns a known reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> KnownReference<T> known(@Nullable Type type, @Nullable T value) {
    return known(type == null ? null : MoreTypes.typeToString(type), value);
  }

  /**
   * Creates and returns a known reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> KnownReference<T> known(@Nullable Class<T> type, @Nullable T value) {
    return known((Type) type, value);
  }

  /**
   * Creates and returns a known reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> KnownReference<T> known(@Nullable TypeLiteral<T> type, @Nullable T value) {
    return known(type == null ? null : type.getType(), value);
  }

  /**
   * Returns the type.
   */
  @Nullable
  String type();

  /**
   * Returns the value.
   */
  @Nullable
  T value();

  /**
   * Checks whether the type of this is equals the given type.
   *
   * @param type the type
   */
  boolean isOfType(Type type);
}
