package com.scofu.common.json;

import com.scofu.common.inject.AbstractFeatureModule;
import com.scofu.common.json.internal.InternalJsonModule;

/**
 * Json module. Binds common adapters.
 */
public class JsonModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    install(new InternalJsonModule());
  }
}
