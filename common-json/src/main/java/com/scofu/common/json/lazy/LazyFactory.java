package com.scofu.common.json.lazy;

import com.jsoniter.any.Any;
import java.util.Map;
import java.util.function.Function;

/** Creates lazy things. */
public interface LazyFactory {

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param any the any
   * @param <T> the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type, Any any);

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param <T> the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type);

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param args the args
   * @param <T> the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type, Map<Function<T, ?>, Object> args);

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param arg1 the arg1
   * @param value1 the value1
   * @param <T> the type of the lazy
   */
  default <T extends Lazy> T create(Class<T> type, Function<T, ?> arg1, Object value1) {
    return create(type, Map.of(arg1, value1));
  }

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param arg1 the arg1
   * @param value1 the value1
   * @param arg2 the arg2
   * @param value2 the value2
   * @param <T> the type of the lazy
   */
  default <T extends Lazy> T create(
      Class<T> type, Function<T, ?> arg1, Object value1, Function<T, ?> arg2, Object value2) {
    return create(type, Map.of(arg1, value1, arg2, value2));
  }

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param arg1 the arg1
   * @param value1 the value1
   * @param arg2 the arg2
   * @param value2 the value2
   * @param arg3 the arg3
   * @param value3 the value3
   * @param <T> the type of the lazy
   */
  default <T extends Lazy> T create(
      Class<T> type,
      Function<T, ?> arg1,
      Object value1,
      Function<T, ?> arg2,
      Object value2,
      Function<T, ?> arg3,
      Object value3) {
    return create(type, Map.of(arg1, value1, arg2, value2, arg3, value3));
  }
}
