package com.scofu.common.inject;

/**
 * Represents a confined feature.
 */
public interface Feature {

  /**
   * See {@link FeatureManager#load(FeatureSet)}.
   */
  default void load() {
  }

  /**
   * See {@link FeatureManager#enable(FeatureSet)}.
   */
  default void enable() {
  }

  /**
   * See {@link FeatureManager#disable(FeatureSet)}.
   */
  default void disable() {
  }
}
