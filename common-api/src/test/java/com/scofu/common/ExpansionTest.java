package com.scofu.common;

import static com.scofu.common.ExpansionMap.expansionMap;
import static com.scofu.common.Identifier.identifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

/** Tests expansions. */
public class ExpansionTest {

  private static final Identifier<Absolute> SELF_IDENTIFIER = identifier("self");
  private static final Identifier<Integer> RANDOM_IDENTIFIER = identifier("random");
  private static final Identifier<String> STRING_IDENTIFIER = identifier("string");

  @Test
  public void test() {
    final var absolute = Absolute.absolute();
    assertTrue(absolute.expansions().isEmpty());
    assertEquals(Optional.empty(), absolute.expand(SELF_IDENTIFIER));
    assertEquals(Optional.empty(), absolute.expand(STRING_IDENTIFIER));

    absolute.map(SELF_IDENTIFIER).to(absolute);
    assertEquals(1, absolute.expansions().size());
    assertSame(absolute, absolute.expand(SELF_IDENTIFIER).orElseThrow());

    absolute.map(RANDOM_IDENTIFIER).toSupplier(() -> ThreadLocalRandom.current().nextInt(0, 100));
    assertEquals(2, absolute.expansions().size());
    final int random = absolute.expand(RANDOM_IDENTIFIER).orElseThrow();
    assertTrue(random >= 0 && random < 100);

    absolute
        .map(STRING_IDENTIFIER)
        .toSupplier(
            () ->
                absolute
                    .expand(RANDOM_IDENTIFIER)
                    .map(integer -> "foo: " + integer)
                    .orElse("foo!"));
    assertEquals(3, absolute.expansions().size());
    final var fooRandom = absolute.expand(STRING_IDENTIFIER).orElseThrow();
    assertTrue(fooRandom.startsWith("foo: "));

    absolute.expansions().remove(RANDOM_IDENTIFIER);
    assertEquals(Optional.empty(), absolute.expand(RANDOM_IDENTIFIER));
    assertEquals(2, absolute.expansions().size());

    final var foo = absolute.expand(STRING_IDENTIFIER).orElseThrow();
    assertEquals("foo!", foo);
  }

  private record Absolute(ExpansionMap expansions) implements Expandable<Absolute> {
    public static Absolute absolute() {
      return new Absolute(expansionMap());
    }
  }
}
