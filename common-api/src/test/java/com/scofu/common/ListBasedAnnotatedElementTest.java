package com.scofu.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.scofu.common.collect.ListBasedAnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Tests {@link ListBasedAnnotatedElementTest}. */
public class ListBasedAnnotatedElementTest {

  @Test
  public void test() {
    final AnnotatedElement element =
        new ListBasedAnnotatedElement(
            Stream.of(Foo.class, Bar.class).<Annotation>map(Impl::new).toList());
    assertTrue(element.isAnnotationPresent(Foo.class));
    assertTrue(element.isAnnotationPresent(Bar.class));
    assertEquals(2, element.getAnnotations().length);
  }

  @Test
  public void testEmpty() {
    final AnnotatedElement element = new ListBasedAnnotatedElement(List.of());
    assertFalse(element.isAnnotationPresent(Foo.class));
    assertFalse(element.isAnnotationPresent(Bar.class));
    assertEquals(0, element.getAnnotations().length);
  }

  private @interface Foo {}

  private @interface Bar {}

  private record Impl(Class<? extends Annotation> annotationType) implements Foo, Bar {}
}
