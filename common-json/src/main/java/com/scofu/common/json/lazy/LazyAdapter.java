package com.scofu.common.json.lazy;

import com.google.inject.Inject;
import com.google.inject.internal.MoreTypes;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.Type;

final class LazyAdapter implements Adapter<Lazy> {

  private final LazyFactory lazyFactory;

  @Inject
  LazyAdapter(LazyFactory lazyFactory) {
    this.lazyFactory = lazyFactory;
  }

  @Override
  public void write(Lazy value, JsonStream jsonStream, Type type) throws IOException {
    jsonStream.writeVal(value.any());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Lazy read(JsonIterator jsonIterator, Type type) throws IOException {
    final var any = jsonIterator.readAny();
    return lazyFactory.create((Class<Lazy>) MoreTypes.getRawType(type), any);
  }
}
