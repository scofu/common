package com.scofu.common.json;

import java.lang.reflect.Type;

/** Converts objects to and from json. */
public interface Json {

  /**
   * Parses and returns a value of the given type from the given bytes.
   *
   * @param type the type
   * @param bytes the bytes
   * @param <T> the type of the value
   */
  <T> T fromBytes(Class<T> type, byte... bytes);

  /**
   * Parses and returns a value of the given type from the given bytes.
   *
   * @param type the type
   * @param bytes the bytes
   * @param <T> the type of the value
   */
  <T> T fromBytes(Type type, byte... bytes);

  /**
   * Translates and returns an array of bytes representing the json of the given value with the
   * given type.
   *
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  <T> byte[] toBytes(Type type, T value);

  /**
   * Parses and returns a value of the given type from the given string.
   *
   * @param type the type
   * @param string the string
   * @param <T> the type of the value
   */
  <T> T fromString(Class<T> type, String string);

  /**
   * Parses and returns a value of the given type from the given string.
   *
   * @param type the type
   * @param string the string
   * @param <T> the type of the value
   */
  <T> T fromString(Type type, String string);

  /**
   * Translates and returns a string representing the json of the given value with the given type.
   *
   * @param type the type
   * @param value the value
   * @param <T> the type of the value
   */
  <T> String toString(Type type, T value);
}
