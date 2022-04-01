package com.scofu.common.json.internal;

import com.google.common.base.MoreObjects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.internal.MoreTypes;
import com.scofu.common.json.TypeCache;
import java.lang.reflect.Type;
import java.util.Optional;

final class InternalTypeCache implements TypeCache {

  private final BiMap<String, Type> types;

  @Inject
  InternalTypeCache() {
    this.types = HashBiMap.create(Maps.newConcurrentMap());
  }

  @Override
  public Optional<Type> asType(String string) {
    return Optional.ofNullable(types.get(string));
  }

  @Override
  public String asString(Type typeLiteral) {
    return types.inverse().computeIfAbsent(typeLiteral, this::toString);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("types", types).toString();
  }

  private String toString(Type typeLiteral) {
    return MoreTypes.typeToString(typeLiteral);
  }

}
