package com.scofu.common.inject.internal;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.scofu.common.inject.Feature;
import com.scofu.common.inject.FeatureBinder;
import com.scofu.common.inject.FeatureManager;

/** Internal feature binder. */
public final class InternalFeatureBinder implements FeatureBinder {

  private final Multibinder<Feature> featureMultibinder;
  private final Multibinder<FeatureManager> featureManagerMultibinder;

  private InternalFeatureBinder(
      Multibinder<Feature> multibinder, Multibinder<FeatureManager> featureManagerMultibinder) {
    this.featureMultibinder = multibinder;
    this.featureManagerMultibinder = featureManagerMultibinder;
  }

  /**
   * Creates and returns a new internal feature binder.
   *
   * @param binder the binder
   */
  public static InternalFeatureBinder newInternalFeatureBinder(Binder binder) {
    return new InternalFeatureBinder(
        Multibinder.newSetBinder(binder.skipSources(InternalFeatureBinder.class), Feature.class),
        Multibinder.newSetBinder(
            binder.skipSources(InternalFeatureBinder.class), FeatureManager.class));
  }

  @Override
  public <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Key<T> key) {
    return featureManagerMultibinder.addBinding().to(key);
  }

  @Override
  public <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(
      TypeLiteral<T> typeLiteral) {
    return featureManagerMultibinder.addBinding().to(typeLiteral);
  }

  @Override
  public <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Class<T> type) {
    return featureManagerMultibinder.addBinding().to(type);
  }

  @Override
  public <T extends FeatureManager> void bindFeatureManagerInstance(T featureManager) {
    featureManagerMultibinder.addBinding().toInstance(featureManager);
  }

  @Override
  public <T extends FeatureManager> void bindFeatureManagerInstance(
      Class<T> type, T featureManager) {
    bindFeatureManagerInstance(featureManager);
  }

  @Override
  public <T extends Feature> ScopedBindingBuilder bindFeature(Key<T> key) {
    return featureMultibinder.addBinding().to(key);
  }

  @Override
  public <T extends Feature> ScopedBindingBuilder bindFeature(TypeLiteral<T> typeLiteral) {
    return featureMultibinder.addBinding().to(typeLiteral);
  }

  @Override
  public <T extends Feature> ScopedBindingBuilder bindFeature(Class<T> type) {
    return featureMultibinder.addBinding().to(type);
  }

  @Override
  public <T extends Feature> void bindFeatureInstance(T feature) {
    featureMultibinder.addBinding().toInstance(feature);
  }

  @Override
  public <T extends Feature> void bindFeatureInstance(Class<T> type, T feature) {
    bindFeatureInstance(feature);
  }
}
