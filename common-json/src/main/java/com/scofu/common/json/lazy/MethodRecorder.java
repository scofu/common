package com.scofu.common.json.lazy;

import static com.google.common.base.Defaults.defaultValue;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/** Captures methods. */
public final class MethodRecorder implements AutoCloseable {

  private final List<Method> methods;
  private boolean finished;

  private MethodRecorder() {
    this.methods = Lists.newLinkedList();
  }

  /**
   * Creates and returns a new method capturer.
   *
   * @param type the type
   * @param functions the functions
   * @param <T> the type
   */
  public static <T> MethodRecorder record(Class<T> type, Collection<Function<T, ?>> functions) {
    final var recorder = new MethodRecorder();
    final var instance =
        type.cast(
            Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[] {type},
                (proxy, method, args) -> {
                  if (recorder.finished) {
                    throw new UnsupportedOperationException();
                  }
                  recorder.methods.add(method);
                  return defaultValue(method.getReturnType());
                }));
    functions.forEach(function -> function.apply(instance));
    return recorder;
  }

  /** Returns the result. */
  public List<Method> methods() {
    return methods;
  }

  @Override
  public void close() {
    finished = true;
  }
}
