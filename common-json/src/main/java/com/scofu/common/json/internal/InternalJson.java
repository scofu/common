package com.scofu.common.json.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Json;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import javax.inject.Inject;

/**
 * Internal json.
 */
@SuppressWarnings("unchecked")
public final class InternalJson implements Json {

  private final Config config;

  @Inject
  InternalJson(Config config) {
    this.config = config;
  }

  @Override
  public <T> T fromBytes(Class<T> type, byte... bytes) {
    checkNotNull(type, "type");
    checkNotNull(bytes, "bytes");
    try {
      return (T) JsonIterator.deserialize(config, bytes, TypeLiteral.create(type));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <T> T fromBytes(Type type, byte... bytes) {
    checkNotNull(type, "type");
    checkNotNull(bytes, "bytes");
    try {
      return (T) JsonIterator.deserialize(config, bytes, TypeLiteral.create(type));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <T> byte[] toBytes(Type type, T value) {
    checkNotNull(type, "type");
    checkNotNull(value, "t");
    try (var outputStream = new ByteArrayOutputStream()) {
      JsonStream.serialize(config, TypeLiteral.create(type), value, outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T fromString(Class<T> type, String string) {
    checkNotNull(type, "type");
    checkNotNull(string, "string");
    try {
      return (T) JsonIterator.deserialize(config, string, TypeLiteral.create(type));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T fromString(Type type, String string) {
    checkNotNull(type, "type");
    checkNotNull(string, "string");
    try {
      return (T) JsonIterator.deserialize(config, string, TypeLiteral.create(type));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <T> String toString(Type type, T value) {
    checkNotNull(type, "type");
    checkNotNull(value, "t");
    return JsonStream.serialize(config, TypeLiteral.create(type), value);
  }
}
