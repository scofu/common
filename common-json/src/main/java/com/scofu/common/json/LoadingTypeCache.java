package com.scofu.common.json;

import com.google.inject.internal.MoreTypes;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * A type cache implementation that loads types using {@link Class#forName(String)}.
 */
public class LoadingTypeCache implements TypeCache {

  private static final LoadingTypeCache INSTANCE = new LoadingTypeCache();

  /**
   * Returns the instance.
   */
  public static LoadingTypeCache instance() {
    return INSTANCE;
  }

  @Override
  public Optional<Type> asType(String string) {
    try {
      return Optional.of(Class.forName(string));
    } catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  @Override
  public String asString(Type typeLiteral) {
    return MoreTypes.getRawType(typeLiteral).getName();
  }
}
