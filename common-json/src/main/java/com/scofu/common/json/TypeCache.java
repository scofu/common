package com.scofu.common.json;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Represents a cache of type and type names.
 */
public interface TypeCache {

  /**
   * Returns the loading type cache instance.
   */
  static TypeCache loading() {
    return LoadingTypeCache.instance();
  }

  Optional<Type> asType(String string);

  String asString(Type typeLiteral);
}
