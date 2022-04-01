package com.scofu.common.json.internal;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;
import com.scofu.common.inject.AbstractFeatureModule;
import com.scofu.common.json.Json;
import com.scofu.common.json.TypeCache;

/**
 * Internal json module. Binds common adapters.
 */
public class InternalJsonModule extends AbstractFeatureModule {

  @Override
  protected void configure() {
    bindFeatureManager(AdapterManager.class).in(Scopes.SINGLETON);
    bindFeature(DynamicReferenceAdapter.class).in(Scopes.SINGLETON);
    bindFeature(KnownReferenceAdapter.class).in(Scopes.SINGLETON);
    bindFeature(InetSocketAddressAdapter.class).in(Scopes.SINGLETON);
    bindFeature(InstantAdapter.class).in(Scopes.SINGLETON);
    bindFeature(ListAdapter.class).in(Scopes.SINGLETON);
    bindFeature(MapAdapter.class).in(Scopes.SINGLETON);
    bindFeature(RecordAdapter.class).in(Scopes.SINGLETON);
    bindFeature(UuidAdapter.class).in(Scopes.SINGLETON);
    bindFeature(PeriodEscapedStringAdapter.class).in(Scopes.SINGLETON);
    bind(Json.class).to(InternalJson.class).in(Scopes.SINGLETON);
    bind(TypeCache.class).to(InternalTypeCache.class).in(Scopes.SINGLETON);
  }

  @Provides
  @Singleton
  Config config() {
    return JsoniterSpi.getDefaultConfig()
        .copyBuilder()
        .escapeUnicode(false)
        .omitDefaultValue(true)
        .build();
  }
}
