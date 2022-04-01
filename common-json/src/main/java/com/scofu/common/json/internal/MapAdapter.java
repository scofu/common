package com.scofu.common.json.internal;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.internal.MoreTypes;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Adapter;
import com.scofu.common.json.Json;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

final class MapAdapter implements Adapter<Map<?, ?>> {

  private static final Class<?> ANY_TYPE;
  private static final Field CACHE_FIELD;

  static {
    try {
      // yoink the cache
      // Hack to replace the map with a linked map, unfortunately we need to do this for every map
      // read because some maps may require a specific order of elements.
      ANY_TYPE = Class.forName("com.jsoniter.any.ObjectLazyAny");
      CACHE_FIELD = ANY_TYPE.getDeclaredField("cache");
      CACHE_FIELD.setAccessible(true);
    } catch (NoSuchFieldException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private final Json json;

  @Inject
  MapAdapter(Json json) {
    this.json = json;
  }

  @Override
  public void write(Map<?, ?> map, JsonStream stream, Type type) throws IOException {
    final var keyType = type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[0]
        : null;
    final var valueType = type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[1]
        : null;

    stream.writeObjectStart();

    final var iterator = map.entrySet().iterator();
    while (iterator.hasNext()) {
      final var element = iterator.next();
      if (element.getKey() instanceof String s) {
        stream.writeObjectField(s);
      } else {
        stream.writeVal(keyType == null ? element.getKey().getClass() : keyType, element.getKey());
        stream.write(':');
      }
      stream.writeVal(valueType == null ? element.getValue().getClass() : valueType,
          element.getValue());
      if (iterator.hasNext()) {
        stream.writeMore();
      }
    }

    stream.writeObjectEnd();
  }

  @Override
  public Map<?, ?> read(JsonIterator iterator, Type type) throws IOException {
    final var any = iterator.readAny();
    if (any == null) {
      return Maps.newLinkedHashMap();
    }
    return decodeMapFromAny(type, any, iterator);
  }

  @SuppressWarnings("unchecked")
  public Map<Object, Object> decodeMapFromAny(Type type, Any any, JsonIterator iterator) {
    final var keyType = TypeLiteral.create(type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[0]
        : Object.class);
    final var valueType = TypeLiteral.create(type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[1]
        : Object.class);

    if (ANY_TYPE.isInstance(any)) {
      try {
        CACHE_FIELD.set(any, Maps.<String, Any>newLinkedHashMapWithExpectedSize(4));
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    final var map = Maps.newLinkedHashMapWithExpectedSize(any.size());

    if (any.valueType() == ValueType.ARRAY) {
      for (var anyElement : any.asList()) {
        final var key = anyElement.get("key").as(keyType);
        final var value = anyElement.get("value").as(valueType);
        map.put(key, value);
      }
    } else {
      final var anyMap = any.asMap();
      final var stringKey = String.class.isAssignableFrom(MoreTypes.getRawType(keyType.getType()));
      for (var entry : anyMap.entrySet()) {
        if (stringKey) {
          map.put(entry.getKey(), entry.getValue().as(valueType));
        } else {
          map.put(json.fromString(keyType.getType(), "\"" + entry.getKey() + "\""),
              entry.getValue().as(valueType));
        }
      }
    }

    return map;
  }
}