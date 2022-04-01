package com.scofu.common.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.internal.MoreTypes;
import org.junit.jupiter.api.Test;

/**
 * Tests type cache.
 */
public class TypeCacheTest {

  @Test
  public void testTypeCacheNameEquality() {
    final var cache = TypeCache.loading();
    final var cacheAsString = cache.asString(BasicType.class);
    assertEquals(cacheAsString, BasicType.AS_STRING);
  }

  @Test
  public void testKnownTypeIsInCache() {
    final var cache = TypeCache.loading();
    assertTrue(cache.asType(BasicType.AS_STRING).isPresent());
  }

  static class BasicType {

    public static final String AS_STRING = MoreTypes.typeToString(BasicType.class);
  }

}
