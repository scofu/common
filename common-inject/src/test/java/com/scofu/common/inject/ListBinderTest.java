package com.scofu.common.inject;

import static com.google.inject.name.Names.named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.util.Types;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ListBinder}.
 */
public class ListBinderTest {

  @SuppressWarnings("unchecked")
  private <T> List<T> createList(String name, Class<T> type,
      Consumer<? super ListBinder<? super T>> consumer) {
    final var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        var listBinder = ListBinder.newListBinder(binder(), type, named(name));
        consumer.accept(listBinder);
      }
    });
    return injector.getInstance(
        (Key<List<T>>) Key.get(Types.newParameterizedType(List.class, type), named(name)));
  }

  @Test
  public void testEquality() {
    final var list = createList("equality", Integer.class, listBinder -> {
      listBinder.addBinding().toInstance(1);
      listBinder.addBinding().toInstance(2);
      listBinder.addBinding().toInstance(3);
    });
    assertEquals(list, List.of(1, 2, 3));
  }

  @Test
  public void testOrdered() {
    final var list = createList("ordered", Integer.class, listBinder -> {
      listBinder.insertFirstBinding().toInstance(3);
      listBinder.insertLastBinding().toInstance(7);
      listBinder.addBinding().toInstance(4);
      listBinder.addBinding().toInstance(5);
      listBinder.insertLastBinding().toInstance(8);
      listBinder.insertFirstBinding().toInstance(2);
      listBinder.addBinding().toInstance(6);
      listBinder.insertFirstBinding().toInstance(1);
      listBinder.insertLastBinding().toInstance(9);
    });
    assertEquals(list, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
    assertNotEquals(list, List.of(9, 8, 7, 6, 5, 4, 3, 2, 1));
  }

  @Test
  public void testOrderedWithDynamicIndex() {
    final var list = createList("ordered", String.class, listBinder -> {
      listBinder.insertBinding(size -> size / 2).toInstance("middle");
      listBinder.addBinding().toInstance("1");
      listBinder.addBinding().toInstance("2");
      listBinder.addBinding().toInstance("3");
      listBinder.addBinding().toInstance("4");
    });
    assertEquals(list, List.of("1", "2", "middle", "3", "4"));
  }

}
