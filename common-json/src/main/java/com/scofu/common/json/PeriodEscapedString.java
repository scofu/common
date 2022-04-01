package com.scofu.common.json;

import java.util.Objects;

/**
 * Represents a string that has escaped periods with
 * <pre>{@link PeriodEscapedString#ESCAPED_PERIOD}</pre>.
 *
 * <p>See {@link Periods#escape(String)}.
 */
public class PeriodEscapedString {

  public static final String ESCAPED_PERIOD = "()";

  private final String string;

  public PeriodEscapedString(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public boolean equals(Object o) {
    return o == this || o instanceof PeriodEscapedString periodEscapedString && string.equals(
        periodEscapedString.string);
  }

  @Override
  public int hashCode() {
    return Objects.hash(string);
  }
}
