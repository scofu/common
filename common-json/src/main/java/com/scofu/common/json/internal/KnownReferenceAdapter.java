package com.scofu.common.json.internal;

import static com.scofu.common.json.KnownReference.known;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.scofu.common.Periods;
import com.scofu.common.json.Adapter;
import com.scofu.common.json.KnownReference;
import java.io.IOException;
import java.lang.reflect.Type;

final class KnownReferenceAdapter implements Adapter<KnownReference<?>> {

  @SuppressWarnings({"ConstantConditions", "unchecked"})
  @Override
  public void write(KnownReference<?> reference, JsonStream jsonStream, Type type)
      throws IOException {
    jsonStream.writeObjectStart();
    jsonStream.writeObjectField(
        reference.type() == null ? "null" : Periods.escape(reference.type()));
    if (reference.type() == null || reference.value() == null) {
      jsonStream.writeNull();
    } else {
      try {
        jsonStream.writeVal(TypeLiteral.create(Class.forName(reference.type())), reference.value());
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    jsonStream.writeObjectEnd();
  }

  @SuppressWarnings("unchecked")
  @Override
  public KnownReference<?> read(JsonIterator jsonIterator, Type type) throws IOException {
    final var any = jsonIterator.readAny().asMap();
    final var entry = any.entrySet().stream().findFirst().orElse(null);
    if (entry == null) {
      throw new IllegalStateException("no entry");
    }
    final var key = Periods.unescape(entry.getKey());
    final Class<?> actualType;
    try {
      actualType = Class.forName(key);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return known(key, null);
    }
    return known(key, entry.getValue().as(TypeLiteral.create(actualType)));
  }
}
