package com.scofu.common.inject;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Binder;

/** An abstract implementation of a feature module. */
public class AbstractFeatureModule implements FeatureModule {

  private Binder binder;
  private FeatureBinder featureBinder;

  protected void configure() {}

  @Override
  public final void configure(Binder binder) {
    checkNotNull(binder, "binder");
    try {
      this.binder = binder.skipSources(AbstractFeatureModule.class);
      this.featureBinder =
          FeatureBinder.newFeatureBinder(binder.skipSources(AbstractFeatureModule.class));
      configure();
    } finally {
      this.binder = null;
      this.featureBinder = null;
    }
  }

  @Override
  public FeatureBinder featureBinder() {
    return checkNotNull(featureBinder, "this method is only available inside configure()");
  }

  @Override
  public Binder binder() {
    return checkNotNull(binder, "this method is only available inside configure()");
  }
}
