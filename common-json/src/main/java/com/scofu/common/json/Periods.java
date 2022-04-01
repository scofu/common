package com.scofu.common.json;

import java.util.regex.Pattern;

/**
 * Utility for escaping and unescaping periods.
 */
public class Periods {

  private Periods() {
  }

  /**
   * Returns a 'period-escaped' version of the given string.
   *
   * <p>Replaces all periods with <pre>{@link PeriodEscapedString#ESCAPED_PERIOD}</pre>.
   *
   * @param string the string
   */
  public static String escape(String string) {
    return string.replaceAll("\\.", PeriodEscapedString.ESCAPED_PERIOD);
  }

  /**
   * Returns a 'period-unescaped' version of the given string.
   *
   * <p>Replaces all <pre>{@link PeriodEscapedString#ESCAPED_PERIOD}</pre> with <pre>.</pre>.
   *
   * @param string the string
   */
  public static String unescape(String string) {
    return string.replaceAll(Pattern.quote(PeriodEscapedString.ESCAPED_PERIOD), ".");
  }

}
