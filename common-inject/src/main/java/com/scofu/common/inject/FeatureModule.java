package com.scofu.common.inject;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * A module that inherits from {@link FeatureBinder} and {@link Binder}.
 */
public interface FeatureModule extends ForwardingFeatureBinder, ForwardingBinder, Module {

}
