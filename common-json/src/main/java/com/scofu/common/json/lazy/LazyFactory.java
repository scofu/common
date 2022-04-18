package com.scofu.common.json.lazy;

import com.jsoniter.any.Any;
import java.util.Map;

/**
 * Creates lazy things.
 */
public interface LazyFactory {

  /**
   * Creates and returns a new lazy.
   *
   * @param type the type
   * @param any  the any
   * @param <T>  the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type, Any any);

  /**
   * Creates and returns a new lazy.
   *
   * @param type     the type
   * @param defaults the defaults
   * @param <T>      the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type, Map<String, Object> defaults);

  /**
   * Creates and retursn a new lazy.
   *
   * @param type the type
   * @param <T>  the type of the lazy
   */
  <T extends Lazy> T create(Class<T> type);
}
