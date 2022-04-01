package com.scofu.common.json.internal;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.scofu.common.json.Adapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

final class InetSocketAddressAdapter implements Adapter<InetSocketAddress> {

  @Override
  public void write(InetSocketAddress inetSocketAddress, JsonStream stream, Type type)
      throws IOException {
    stream.writeVal(String.format("%s:%d", Optional.ofNullable(inetSocketAddress.getAddress())
        .map(InetAddress::getHostAddress)
        .orElseGet(inetSocketAddress::getHostName), inetSocketAddress.getPort()));
  }

  @Override
  public InetSocketAddress read(JsonIterator iterator, Type type) throws IOException {
    final var split = iterator.readString().split(":", 2);
    return InetSocketAddress.createUnresolved(split[0], Integer.parseInt(split[1]));
  }
}