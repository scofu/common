package com.scofu.common.inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.Guice;
import com.scofu.common.inject.FeatureLifecycleTest.StatefulFeature.State;
import org.junit.jupiter.api.Test;

/**
 * Tests the lifecycle of features.
 */
public class FeatureLifecycleTest {

  @Test
  public void testLifecycle() {
    final var injector = Guice.createInjector(new AbstractFeatureModule() {
      @Override
      protected void configure() {
        final var feature = new StatefulFeature();
        bind(State.class).toProvider(feature::state);
        bindFeatureInstance(feature);
        bindFeatureManagerInstance(new StatefulManager());
      }
    });
    final var state = injector.getProvider(State.class);
    final var bootstrap = injector.getInstance(FeatureBootstrap.class);
    assertEquals(state.get(), State.CONSTRUCTED);
    bootstrap.load();
    assertEquals(state.get(), State.LOADED);
    bootstrap.enable();
    assertEquals(state.get(), State.ENABLED);
    bootstrap.disable();
    assertEquals(state.get(), State.DISABLED);
  }

  static class StatefulFeature implements Feature {

    private State state = State.CONSTRUCTED;

    @Override
    public void load() {
      state = State.LOADED;
    }

    @Override
    public void enable() {
      state = State.ENABLED;
    }

    @Override
    public void disable() {
      state = State.DISABLED;
    }

    public State state() {
      return state;
    }

    enum State {
      CONSTRUCTED, LOADED, ENABLED, DISABLED
    }
  }

  static class StatefulManager extends AbstractFeatureManager {

    @Override
    protected void load() {
      forEach(Feature::load);
    }

    @Override
    protected void enable() {
      forEach(Feature::enable);
    }

    @Override
    protected void disable() {
      forEach(Feature::disable);
    }
  }

}
