package com.scofu.common.inject;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.ScopedBindingBuilder;
import com.scofu.common.inject.internal.InternalFeatureBinder;

/**
 * Binds a set of features.
 */
public interface FeatureBinder {

  /**
   * Creates and returns a new feature binder.
   *
   * @param binder the binder
   */
  static FeatureBinder newFeatureBinder(Binder binder) {
    checkNotNull(binder, "binder");
    return InternalFeatureBinder.newInternalFeatureBinder(binder.skipSources(FeatureBinder.class));
  }

  /**
   * Binds a feature manager.
   *
   * @param key the key
   * @param <T> the type of the feature manager
   */
  <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Key<T> key);

  /**
   * Binds a feature manager.
   *
   * @param typeLiteral the type literal
   * @param <T>         the type of the feature manager
   */
  <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(TypeLiteral<T> typeLiteral);

  /**
   * Binds a feature manager.
   *
   * @param type the type
   * @param <T>  the type of the feature manager
   */
  <T extends FeatureManager> ScopedBindingBuilder bindFeatureManager(Class<T> type);

  /**
   * Binds a feature manager instance.
   *
   * @param featureManager the feature manager
   * @param <T>            the type of the feature manager
   */
  <T extends FeatureManager> void bindFeatureManagerInstance(T featureManager);

  /**
   * Binds a feature manager instance.
   *
   * @param type           the type
   * @param featureManager the feature manager
   * @param <T>            the type of the feature manager
   */
  <T extends FeatureManager> void bindFeatureManagerInstance(Class<T> type, T featureManager);

  /**
   * Binds a feature.
   *
   * @param key the key
   * @param <T> the type of the feature
   */
  <T extends Feature> ScopedBindingBuilder bindFeature(Key<T> key);

  /**
   * Binds a feature.
   *
   * @param typeLiteral the type literal
   * @param <T>         the type of the feature
   */
  <T extends Feature> ScopedBindingBuilder bindFeature(TypeLiteral<T> typeLiteral);

  /**
   * Binds a feature.
   *
   * @param type the type
   * @param <T>  the type of the feature
   */
  <T extends Feature> ScopedBindingBuilder bindFeature(Class<T> type);

  /**
   * Binds a feature instance.
   *
   * @param feature the feature
   * @param <T>     the type of the feature
   */
  <T extends Feature> void bindFeatureInstance(T feature);

  /**
   * Binds a feature instance.
   *
   * @param type    the type
   * @param feature the feature
   * @param <T>     the type of the feature
   */
  <T extends Feature> void bindFeatureInstance(Class<T> type, T feature);
}
