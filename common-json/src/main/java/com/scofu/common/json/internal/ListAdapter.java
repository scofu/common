package com.scofu.common.json.internal;

import com.google.common.collect.Lists;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

final class ListAdapter implements Adapter<List<?>> {

  @Override
  public void write(List<?> list, JsonStream stream, Type type) throws IOException {
    stream.writeArrayStart();
    final var elementType = type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[0]
        : null;
    final var iterator = list.iterator();
    while (iterator.hasNext()) {
      final var element = iterator.next();
      stream.writeVal(elementType == null ? element.getClass() : elementType, element);
      if (iterator.hasNext()) {
        stream.writeMore();
      }
    }
    stream.writeArrayEnd();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<?> read(JsonIterator iterator, Type type) throws IOException {
    final var elementType = TypeLiteral.create(type instanceof ParameterizedType parameterizedType
        ? parameterizedType.getActualTypeArguments()[0]
        : Object.class);
    final var any = iterator.readAny();
    if (any == null) {
      return Lists.newArrayList();
    }
    return any.asList()
        .stream()
        .map(element -> element.as(elementType))
        .collect(Collectors.toCollection(Lists::newArrayList));
  }
}