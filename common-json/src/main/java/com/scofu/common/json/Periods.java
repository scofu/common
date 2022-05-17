package com.scofu.common.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

/** Utility for escaping and unescaping periods. */
public class Periods {

  private Periods() {}

  /**
   * Returns a 'period-escaped' version of the given string.
   *
   * <p>Replaces all periods with {@link PeriodEscapedString#ESCAPED_PERIOD}.
   *
   * @param string the string
   */
  public static String escape(String string) {
    checkNotNull(string, "string");
    return string.replaceAll("\\.", PeriodEscapedString.ESCAPED_PERIOD);
  }

  /**
   * Returns a 'period-unescaped' version of the given string.
   *
   * <p>Replaces all periods with {@link PeriodEscapedString#ESCAPED_PERIOD}.
   *
   * @param string the string
   */
  public static String unescape(String string) {
    checkNotNull(string, "string");
    return string.replaceAll(Pattern.quote(PeriodEscapedString.ESCAPED_PERIOD), ".");
  }
}
