package com.scofu.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.scofu.common.reflect.Lambdas;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Tests {@link Lambdas}. */
public class LambdasTest {

  @Test
  public void testLambdas() {
    assertEquals(List.of(Integer.class, Void.TYPE), resolve((Integer integer) -> {}));
    assertEquals(List.of(Integer.class), resolve(() -> 1));
    assertEquals(List.of(Float.class), resolve(() -> 1F));
    assertEquals(List.of(String.class, Integer.class), resolve((String string) -> string.length()));
    assertEquals(List.of(String.class, Integer.class), resolve(String::length));
    assertEquals(
        List.of(Double.class, Integer.class, Double.class),
        resolve((Double a, Integer b) -> a + b));
    assertEquals(
        List.of(Float.class, Double.class, Integer.class),
        resolve((Float a, Double b) -> (int) (a + b)));
  }

  private <T> List<Class<?>> resolve(Consumer<T> consumer) {
    return Lambdas.resolveTypes(consumer).toList();
  }

  private <T> List<Class<?>> resolve(Supplier<T> supplier) {
    return Lambdas.resolveTypes(supplier).toList();
  }

  private <T, R> List<Class<?>> resolve(Function<T, R> function) {
    return Lambdas.resolveTypes(function).toList();
  }

  private <T, U, R> List<Class<?>> resolve(BiFunction<T, U, R> function) {
    return Lambdas.resolveTypes(function).toList();
  }

  @FunctionalInterface
  interface Consumer<T> extends Serializable {
    void toVoid(T t);
  }

  @FunctionalInterface
  interface Supplier<T> extends Serializable {
    T toT();
  }

  @FunctionalInterface
  interface Function<T, R> extends Serializable {
    R toR(T t);
  }

  @FunctionalInterface
  interface BiFunction<T, U, R> extends Serializable {
    R toR(T t, U u);
  }
}
