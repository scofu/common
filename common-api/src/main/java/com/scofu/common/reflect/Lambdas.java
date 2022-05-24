package com.scofu.common.reflect;

import java.io.Serializable;
import java.lang.constant.ClassDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.SerializedLambda;
import java.util.stream.Stream;

/** Lambda utilities. */
public class Lambdas {

  private Lambdas() {}

  /**
   * Returns a stream of resolved types from the given lambda.
   *
   * @param lambda the lambda
   * @param <T> the type of the lambda
   */
  public static <T extends Serializable> Stream<Class<?>> resolveTypes(T lambda) {
    try {
      final var lookup = MethodHandles.lookup();
      final var method = lambda.getClass().getDeclaredMethod("writeReplace");
      method.setAccessible(true);
      final var serializedLambda = (SerializedLambda) lookup.unreflect(method).invoke(lambda);
      // TODO: find static method handle in the impl class and return types from that.
      final var signatures =
          serializedLambda.getInstantiatedMethodType().replaceAll("\\((.*)\\)", "$1").split(";");
      return Stream.of(signatures)
          .map(
              signature ->
                  signature.length() == 1 // primitive
                      ? ClassDesc.ofDescriptor(signature)
                      : ClassDesc.ofDescriptor(signature + ";"))
          .map(
              classDesc -> {
                try {
                  return (Class<?>) classDesc.resolveConstantDesc(lookup);
                } catch (ReflectiveOperationException e) {
                  throw new RuntimeException(e);
                }
              });
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
