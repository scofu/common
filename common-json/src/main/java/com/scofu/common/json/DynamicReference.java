package com.scofu.common.json;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.TypeLiteral;
import com.scofu.common.json.internal.InternalDynamicReference;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/**
 * Represents a dynamic reference.
 *
 * <p>A dynamic reference is ...
 *
 * <p>The difference between this and {@link KnownReference} is that this' type is resolved
 * dynamically from a {@link TypeCache}, whereas {@link KnownReference}'s type is just by class
 * name.
 *
 * @param <T> the type of the value
 */
public interface DynamicReference<T> {

  /**
   * Creates and returns a dynamic reference from the given type and value.
   *
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> DynamicReference<T> dynamic(@Nullable String type, @Nullable T value) {
    return InternalDynamicReference.newInternalDynamicReference(type, value);
  }

  /**
   * Creates and returns a dynamic reference with the given type cache and value.
   *
   * @param typeCache the type cache
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> DynamicReference<T> dynamic(TypeCache typeCache, @Nullable T value) {
    return dynamic(typeCache, value == null ? null : value.getClass(), value);
  }

  /**
   * Creates and returns a dynamic reference with the given type cache, type and value.
   *
   * @param typeCache the type cache
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> DynamicReference<T> dynamic(
      TypeCache typeCache, @Nullable Type type, @Nullable T value) {
    checkNotNull(typeCache, "typeCache");
    return dynamic(type == null ? null : typeCache.asString(type), value);
  }

  /**
   * Creates and returns a dynamic reference with the given type cache, type and value.
   *
   * @param typeCache the type cache
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> DynamicReference<T> dynamic(
      TypeCache typeCache, @Nullable Class<T> type, @Nullable T value) {
    return dynamic(typeCache, (Type) type, value);
  }

  /**
   * Creates and returns a dynamic reference with the given type cache, type and value.
   *
   * @param typeCache the type cache
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  static <T> DynamicReference<T> dynamic(
      TypeCache typeCache, @Nullable TypeLiteral<T> type, @Nullable T value) {
    return dynamic(typeCache, type == null ? null : type.getType(), value);
  }

  /** Returns the type. */
  @Nullable
  String type();

  /** Returns the value. */
  @Nullable
  T value();
}
