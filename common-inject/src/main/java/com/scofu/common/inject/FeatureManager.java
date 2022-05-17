package com.scofu.common.inject;

/** Manages bound features. */
public interface FeatureManager {

  /**
   * Selectively loads features from the given feature set.
   *
   * @param features the features
   */
  void load(FeatureSet features);

  /**
   * Selectively enables features from the given feature set.
   *
   * @param features the features
   */
  void enable(FeatureSet features);

  /**
   * Selectively disables features from the given feature set.
   *
   * @param features the features
   */
  void disable(FeatureSet features);
}
