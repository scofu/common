package com.scofu.common.json.internal;

import static com.scofu.common.json.DynamicReference.dynamic;

import com.google.inject.Inject;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.json.Adapter;
import com.scofu.common.json.DynamicReference;
import com.scofu.common.json.TypeCache;
import java.io.IOException;
import java.lang.reflect.Type;

final class DynamicReferenceAdapter implements Adapter<DynamicReference<?>> {

  private final TypeCache typeCache;

  @Inject
  DynamicReferenceAdapter(TypeCache typeCache) {
    this.typeCache = typeCache;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void write(DynamicReference<?> reference, JsonStream jsonStream, Type type)
      throws IOException {
    jsonStream.writeObjectStart();
    jsonStream.writeObjectField(reference.type() == null ? "null" : reference.type());
    if (reference.type() == null || reference.value() == null) {
      jsonStream.writeNull();
    } else {
      jsonStream.writeVal(
          TypeLiteral.create(typeCache.asType(reference.type()).orElseThrow()), reference.value());
    }
    jsonStream.writeObjectEnd();
  }

  @SuppressWarnings("unchecked")
  @Override
  public DynamicReference<?> read(JsonIterator jsonIterator, Type type) throws IOException {
    final var any = jsonIterator.readAny().asMap();
    final var entry = any.entrySet().stream().findFirst().orElse(null);
    if (entry == null) {
      throw new IllegalStateException("no entry");
    }
    final var actualType = typeCache.asType(entry.getKey()).orElse(null);
    if (actualType == null) {
      return dynamic(typeCache, null);
    }
    return dynamic(entry.getKey(), entry.getValue().as(TypeLiteral.create(actualType)));
  }
}
