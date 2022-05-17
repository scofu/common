package com.scofu.common.inject;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;

/** Forwards a {@link FeatureBinder}. */
public interface ForwardingFeatureBinder extends FeatureBinder {

  /** Returns the forwarded feature binder. */
  FeatureBinder featureBinder();

  @Override
  default <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Key<T> key) {
    return featureBinder().bindFeatureManager(key);
  }

  @Override
  default <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(
      TypeLiteral<T> typeLiteral) {
    return featureBinder().bindFeatureManager(typeLiteral);
  }

  @Override
  default <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Class<T> type) {
    return featureBinder().bindFeatureManager(type);
  }

  @Override
  default <T extends FeatureManager> void bindFeatureManagerInstance(T featureManager) {
    featureBinder().bindFeatureManagerInstance(featureManager);
  }

  @Override
  default <T extends FeatureManager> void bindFeatureManagerInstance(
      Class<T> type, T featureManager) {
    featureBinder().bindFeatureManagerInstance(type, featureManager);
  }

  @Override
  default <T extends Feature> ScopedBindingBuilder bindFeature(Key<T> key) {
    return featureBinder().bindFeature(key);
  }

  @Override
  default <T extends Feature> ScopedBindingBuilder bindFeature(TypeLiteral<T> typeLiteral) {
    return featureBinder().bindFeature(typeLiteral);
  }

  @Override
  default <T extends Feature> ScopedBindingBuilder bindFeature(Class<T> type) {
    return featureBinder().bindFeature(type);
  }

  @Override
  default <T extends Feature> void bindFeatureInstance(T feature) {
    featureBinder().bindFeatureInstance(feature);
  }

  @Override
  default <T extends Feature> void bindFeatureInstance(Class<T> type, T feature) {
    featureBinder().bindFeatureInstance(type, feature);
  }
}
