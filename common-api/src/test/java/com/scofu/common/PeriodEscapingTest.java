package com.scofu.common;

import static com.scofu.common.PeriodEscapedString.ESCAPED_PERIOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests period escaping and unescaping. */
public class PeriodEscapingTest {

  @Test
  public void testEscape() {
    final var unescaped = "test.scofu.com";
    final var escaped = Periods.escape(unescaped);
    assertEquals(escaped, "test" + ESCAPED_PERIOD + "scofu" + ESCAPED_PERIOD + "com");
  }

  @Test
  public void testUnescape() {
    final var escaped = "test" + ESCAPED_PERIOD + "scofu" + ESCAPED_PERIOD + "com";
    final var unescaped = Periods.unescape(escaped);
    assertEquals(unescaped, "test.scofu.com");
  }

  @Test
  public void testEscapeUnescape() {
    assertEquals("test.scofu.com", Periods.unescape(Periods.escape("test.scofu.com")));
  }
}
