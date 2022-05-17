package com.scofu.common.json.internal;

import com.google.inject.internal.MoreTypes;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Optional;

final class RecordAdapter implements Adapter<Record> {

  @Override
  public void write(Record record, JsonStream jsonStream, Type type) throws IOException {
    jsonStream.writeObjectStart();
    final var recordComponents = record.getClass().getRecordComponents();
    try {
      var needsMore = false;
      for (var recordComponent : recordComponents) {
        needsMore = writeRecordComponent(record, jsonStream, needsMore, recordComponent);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
    jsonStream.writeObjectEnd();
  }

  @Override
  public Record read(JsonIterator jsonIterator, Type type) throws IOException {
    final var recordClass = MoreTypes.getRawType(type);
    final var components = recordClass.getRecordComponents();
    final var objects = new Object[components.length];
    final var any = jsonIterator.readAny();
    if (any == null) {
      return constructRecord(recordClass, objects);
    }
    readRecordComponents(components, objects, any);
    return constructRecord(recordClass, objects);
  }

  @SuppressWarnings("unchecked")
  private void readRecordComponents(RecordComponent[] components, Object[] objects, Any any) {
    for (var i = 0; i < components.length; i++) {
      final var component = components[i];
      var name = component.getName();
      if (component.getAccessor().isAnnotationPresent(JsonProperty.class)) {
        final var property = component.getAccessor().getAnnotation(JsonProperty.class);
        name = property.value().isEmpty() ? name : property.value();
      }
      final var object = any.asMap().get(name);
      if (object != null) {
        objects[i] = object.as(TypeLiteral.create(component.getGenericType()));
      }
    }
  }

  private Record constructRecord(Class<?> recordClass, Object[] objects) {
    try {
      final var constructor = recordClass.getDeclaredConstructors()[0];
      constructor.setAccessible(true);
      return (Record) constructor.newInstance(objects);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  private boolean writeRecordComponent(
      Record record, JsonStream jsonStream, boolean needsMore, RecordComponent recordComponent)
      throws IllegalAccessException, InvocationTargetException, IOException {
    final var accessor = recordComponent.getAccessor();
    accessor.setAccessible(true);
    final var object = accessor.invoke(record);
    if (object == null) {
      return needsMore; // continue
    }
    if (needsMore) {
      jsonStream.writeMore();
    }
    final var key =
        Optional.ofNullable(accessor.getAnnotation(JsonProperty.class))
            .map(JsonProperty::value)
            .orElseGet(recordComponent::getName);
    jsonStream.writeObjectField(key);
    jsonStream.writeVal(recordComponent.getGenericType(), object);
    return true;
  }
}
