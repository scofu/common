package com.scofu.common;

import com.google.common.collect.ForwardingMap;
import java.util.Map;

class ForwardingExpansionMap extends ForwardingMap<Identifier<?>, Expansion<?>>
    implements ExpansionMap {

  private final Map<Identifier<?>, Expansion<?>> delegate;

  ForwardingExpansionMap(Map<Identifier<?>, Expansion<?>> delegate) {
    this.delegate = delegate;
  }

  @Override
  protected Map<Identifier<?>, Expansion<?>> delegate() {
    return delegate;
  }
}
