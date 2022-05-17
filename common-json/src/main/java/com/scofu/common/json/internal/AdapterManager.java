package com.scofu.common.json.internal;

import com.google.inject.internal.MoreTypes;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.scofu.common.inject.AbstractFeatureManager;
import com.scofu.common.json.Adapter;
import java.lang.reflect.Type;
import java.util.Map;

/** Manages {@link Adapter} features. */
@SuppressWarnings({"unchecked", "rawtypes", "UnstableApiUsage"})
public final class AdapterManager extends AbstractFeatureManager {

  private static final Map<Class, Decoder> NATIVE_DECODERS;

  static {
    try {
      final var type = Class.forName("com.jsoniter.CodegenImplNative");
      final var field = type.getDeclaredField("NATIVE_DECODERS");
      field.setAccessible(true);
      NATIVE_DECODERS = (Map<Class, Decoder>) field.get(null);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

  @Override
  protected void load() {
    System.out.println("loading adapters");
    streamWithType(Adapter.class).forEach(this::bind);
  }

  private void bind(Adapter adapter) {
    System.out.println("binding " + adapter);
    final var typeArgument = adapter.typeArgument();

    JsoniterSpi.registerExtension(
        new EmptyExtension() {
          @Override
          public Decoder createDecoder(String cacheKey, Type type) {
            if (cacheKey.endsWith(".original")) {
              return null;
            }
            if (!typeArgument.getRawType().isAssignableFrom(MoreTypes.getRawType(type))) {
              return null;
            }
            return iterator -> adapter.read(iterator, type);
          }

          @Override
          public Encoder createEncoder(String cacheKey, Type type) {
            if (cacheKey.endsWith(".original")) {
              return null;
            }
            if (!typeArgument.getRawType().isAssignableFrom(MoreTypes.getRawType(type))) {
              return null;
            }
            return (o, stream) -> adapter.write(o, stream, type);
          }
        });
    NATIVE_DECODERS.put(
        typeArgument.getRawType(), iter -> adapter.read(iter, typeArgument.getRawType()));
    JsoniterSpi.registerMapKeyDecoder(
        typeArgument.getType(), iterator -> adapter.read(iterator, typeArgument.getType()));
    JsoniterSpi.registerMapKeyEncoder(
        typeArgument.getType(), (o, stream) -> adapter.write(o, stream, typeArgument.getType()));
  }
}
