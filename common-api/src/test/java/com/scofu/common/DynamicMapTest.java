package com.scofu.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.scofu.common.collect.ConcurrentDynamicMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/** Tests {@link com.scofu.common.collect.DynamicMap}. */
public class DynamicMapTest {

  record User(String id) implements Predicate<String> {

    @Override
    public boolean test(String s) {
      return s.equals(id);
    }
  }

  @Test
  public void testSingleValue() {
    final var map = new ConcurrentDynamicMap<String, User>();
    assertTrue(map.unmappedValues().isEmpty());
    assertTrue(map.mappedValues().isEmpty());
    assertEquals(Optional.empty(), map.get("id"));
    assertTrue(map.mappedValues().isEmpty());

    final var user = new User("id");
    map.add(user);
    assertEquals(1, map.unmappedValues().size());
    assertTrue(map.mappedValues().isEmpty());

    assertSame(user, map.get("id").orElseThrow());
    assertEquals(1, map.mappedValues().size());

    assertSame(user, map.invalidate("id").orElseThrow());
    assertEquals(1, map.unmappedValues().size());
    assertTrue(map.mappedValues().isEmpty());
    assertSame(user, map.get("id").orElseThrow());
  }

  @Test
  public void testMultipleValues() {
    final var map = new ConcurrentDynamicMap<String, User>();
    final var amount = 10;
    final var users =
        IntStream.range(0, amount).mapToObj(Integer::toString).map(User::new).toList();
    users.forEach(map::add);
    assertEquals(amount, map.unmappedValues().size());
    assertTrue(map.mappedValues().isEmpty());

    for (var i = 0; i < users.size(); i++) {
      final var user = users.get(i);
      assertSame(user, map.get(Integer.toString(i)).orElseThrow());
      assertEquals(i + 1, map.mappedValues().size());
    }
    assertEquals(amount, map.mappedValues().size());

    for (var i = amount - 1; i >= 0; i--) {
      final var user = users.get(i);
      assertSame(user, map.invalidate(Integer.toString(i)).orElseThrow());
      assertEquals(i, map.mappedValues().size());
    }
    assertTrue(map.mappedValues().isEmpty());
  }
}
