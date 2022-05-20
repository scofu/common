package com.scofu.common;

import com.google.common.collect.Maps;
import java.util.Map;

/** Map of expansions. */
public interface ExpansionMap extends Map<Identifier<?>, Expansion<?>> {

  /** Creates and returns a new expansion map. */
  static ExpansionMap expansionMap() {
    return new ForwardingExpansionMap(Maps.newConcurrentMap());
  }
}
