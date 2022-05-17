package com.scofu.common.inject;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.common.inject.internal.InternalFeatureSet;
import java.util.Set;
import java.util.stream.Stream;

/** Represents a set of features. */
public interface FeatureSet extends Iterable<Feature> {

  /**
   * Wraps and returns the given set of features.
   *
   * @param features the features
   */
  static FeatureSet of(Set<Feature> features) {
    checkNotNull(features, "features");
    return InternalFeatureSet.newInternalFeatureSet(features);
  }

  /**
   * Returns a stream of features of the given type.
   *
   * @param type the type
   * @param <T> the type
   */
  <T> Stream<T> streamWithType(Class<T> type);
}
