package com.scofu.common.json;

import com.google.common.reflect.TypeToken;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.inject.Feature;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Adapts objects with the given type to and from json.
 *
 * @param <T> the type of the objects
 */
public interface Adapter<T> extends Feature {

  /**
   * Returns the type argument.
   */
  @SuppressWarnings({"unchecked", "UnstableApiUsage"})
  default TypeToken<T> typeArgument() {
    if (getClass().getGenericInterfaces()[0] instanceof ParameterizedType parameterizedType) {
      return (TypeToken<T>) TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
    }
    throw new IllegalStateException("Type has been erased.");
  }

  /**
   * Writes the given value to the given json stream.
   *
   * @param value      the value
   * @param jsonStream the json stream
   * @param type       the type of the value
   * @throws IOException json stream exception
   */
  void write(T value, JsonStream jsonStream, Type type) throws IOException;

  /**
   * Reads and returns a value from the given json iterator with the given type.
   *
   * @param jsonIterator the json iterator
   * @param type         the type
   * @throws IOException json iterator exception
   */
  T read(JsonIterator jsonIterator, Type type) throws IOException;

}
