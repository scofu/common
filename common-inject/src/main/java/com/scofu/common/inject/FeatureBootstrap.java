package com.scofu.common.inject;

import java.util.Set;
import javax.inject.Inject;

/** Bootstraps bound feature managers. */
public final class FeatureBootstrap implements Feature {

  private final Set<FeatureManager> managers;
  private final FeatureSet features;

  @Inject
  FeatureBootstrap(Set<FeatureManager> managers, Set<Feature> features) {
    this.managers = managers;
    this.features = FeatureSet.of(features);
  }

  @Override
  public void load() {
    managers.forEach(featureManager -> featureManager.load(features));
  }

  @Override
  public void enable() {
    managers.forEach(featureManager -> featureManager.enable(features));
  }

  @Override
  public void disable() {
    managers.forEach(featureManager -> featureManager.disable(features));
  }
}
