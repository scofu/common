package com.scofu.common.json.lazy;

import com.google.inject.Scopes;
import com.scofu.common.inject.AbstractFeatureModule;

/**
 * Lazy json module.
 */
public class LazyJsonModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    bind(LazyFactory.class).to(InternalLazyFactory.class).in(Scopes.SINGLETON);
    bindFeature(LazyAdapter.class).in(Scopes.SINGLETON);
  }
}
