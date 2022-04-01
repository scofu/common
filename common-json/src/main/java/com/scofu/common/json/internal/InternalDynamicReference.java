package com.scofu.common.json.internal;

import com.scofu.common.json.DynamicReference;
import javax.annotation.Nullable;

/**
 * Internal dynamic reference.
 *
 * @param <T> the type of the value
 */
@SuppressWarnings("ClassCanBeRecord")
public class InternalDynamicReference<T> implements DynamicReference<T> {

  private final String type;
  private final T value;

  private InternalDynamicReference(String type, T value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Creates and returns an internal dynamic reference from the given type and value.
   *
   * @param type  the type
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> InternalDynamicReference<T> newInternalDynamicReference(String type, T value) {
    return new InternalDynamicReference<T>(type, value);
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
}
