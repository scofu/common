package com.scofu.common.inject;

import java.util.Iterator;
import java.util.stream.Stream;

/** Forwards a {@link FeatureSet}. */
public interface ForwardingFeatureSet extends FeatureSet {

  /** Returns the bound features. */
  FeatureSet features();

  @Override
  default <T> Stream<T> streamWithType(Class<T> type) {
    return features().streamWithType(type);
  }

  @Override
  default Iterator<Feature> iterator() {
    return features().iterator();
  }
}
