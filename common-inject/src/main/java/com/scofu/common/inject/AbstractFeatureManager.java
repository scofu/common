package com.scofu.common.inject;

import static com.google.common.base.Preconditions.checkNotNull;

/** An abstract implementation of a feature manager. */
public class AbstractFeatureManager implements FeatureManager, ForwardingFeatureSet {

  private FeatureSet features;

  protected void load() {}

  @Override
  public final void load(FeatureSet features) {
    checkNotNull(features, "features");
    try {
      this.features = features;
      load();
    } finally {
      this.features = null;
    }
  }

  @Override
  public final void enable(FeatureSet features) {
    checkNotNull(features, "features");
    try {
      this.features = features;
      enable();
    } finally {
      this.features = null;
    }
  }

  protected void enable() {}

  protected void disable() {}

  @Override
  public final void disable(FeatureSet features) {
    checkNotNull(features, "features");
    try {
      this.features = features;
      disable();
    } finally {
      this.features = null;
    }
  }

  @Override
  public final FeatureSet features() {
    return checkNotNull(
        features, "this method is only available inside load(), enable() and disable()");
  }
}
