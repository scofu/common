package com.scofu.common.inject.internal;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nullable;

@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@interface Internal {

  int annotationHashCode();

  @SuppressWarnings("ClassExplicitlyAnnotation")
  record Access(int annotationHashCode) implements Internal {

    public static Access get() {
      return new Access(0);
    }

    public static Access get(@Nullable Annotation annotation) {
      return new Access(annotation == null ? 0 : annotation.hashCode());
    }

    @Override
    public int annotationHashCode() {
      return (127 * "annotationHashCode".hashCode()) ^ annotationHashCode;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
      return Internal.class;
    }

    @Override
    public int hashCode() {
      return annotationHashCode;
    }

    @Override
    public boolean equals(Object obj) {
      return obj == this
          || obj instanceof Access access && annotationHashCode == access.annotationHashCode;
    }

  }

}
