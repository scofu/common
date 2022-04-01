package com.scofu.common.json.internal;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;

final class InstantAdapter implements Adapter<Instant> {

  @Override
  public void write(Instant instant, JsonStream stream, Type type) throws IOException {
    stream.writeVal(instant.toEpochMilli());
  }

  @Override
  public Instant read(JsonIterator iterator, Type type) throws IOException {
    return Instant.ofEpochMilli(iterator.readLong());
  }
}