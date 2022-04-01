package com.scofu.common.inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.Guice;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link FeatureBootstrap}.
 */
public class FeatureBootstrapTest {

  @Test
  public void testBootstrap() {
    interface Nothing extends Feature {

      void doSomething();
    }

    final var greeted = new AtomicBoolean();
    final var injector = Guice.createInjector(new AbstractFeatureModule() {
      @Override
      protected void configure() {
        bindFeatureInstance((Nothing) () -> greeted.set(true));
        bindFeatureManagerInstance(new AbstractFeatureManager() {
          @Override
          protected void load() {
            streamWithType(Nothing.class).forEach(Nothing::doSomething);
          }
        });
      }
    });
    final var bootstrap = injector.getInstance(FeatureBootstrap.class);
    assertFalse(greeted.get());
    bootstrap.load();
    assertTrue(greeted.get());
  }

}
