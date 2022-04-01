package com.scofu.common.json.internal;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

final class UuidAdapter implements Adapter<UUID> {

  @Override
  public void write(UUID uuid, JsonStream jsonStream, Type type) throws IOException {
    jsonStream.writeVal(uuid.toString());
  }

  @Override
  public UUID read(JsonIterator jsonIterator, Type type) throws IOException {
    return UUID.fromString(jsonIterator.readString());
  }
}
