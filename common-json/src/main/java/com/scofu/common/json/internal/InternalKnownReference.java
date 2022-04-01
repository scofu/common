package com.scofu.common.json.internal;

import com.google.inject.internal.MoreTypes;
import com.scofu.common.json.KnownReference;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

/**
 * Internal known reference.
 *
 * @param <T> the type of the value
 */
@SuppressWarnings("ClassCanBeRecord")
public class InternalKnownReference<T> implements KnownReference<T> {

  private final String type;
  private final T value;

  private InternalKnownReference(String type, T value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Creates and returns an internal known reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> InternalKnownReference<T> newInternalKnownReference(String type, T value) {
    return new InternalKnownReference<T>(type, value);
  }

  @Override
  @Nullable
  public String type() {
    return type;
  }

  @Override
  @Nullable
  public T value() {
    return value;
  }

  @Override
  public boolean isOfType(Type type) {
    return MoreTypes.typeToString(type).equals(this.type);
  }
}
