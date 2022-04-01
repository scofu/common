package com.scofu.common.inject.internal;

import com.google.common.collect.ForwardingSet;
import com.scofu.common.inject.Feature;
import com.scofu.common.inject.FeatureSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Internal feature set.
 */
public final class InternalFeatureSet extends ForwardingSet<Feature> implements FeatureSet {

  private final Set<Feature> delegate;

  private InternalFeatureSet(Set<Feature> delegate) {
    this.delegate = delegate;
  }

  /**
   * Creates and returns a new internal feature set.
   *
   * @param delegate the delegate
   */
  public static InternalFeatureSet newInternalFeatureSet(Set<Feature> delegate) {
    return new InternalFeatureSet(delegate);
  }

  @Override
  public <T> Stream<T> streamWithType(Class<T> type) {
    return delegate().stream().filter(type::isInstance).map(type::cast);
  }

  @Override
  protected Set<Feature> delegate() {
    return delegate;
  }
}
