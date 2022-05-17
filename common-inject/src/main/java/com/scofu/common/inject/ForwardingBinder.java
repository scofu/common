package com.scofu.common.inject;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.Message;
import com.google.inject.spi.ModuleAnnotatedMethodScanner;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

/** Forwards a {@link Binder}. */
public interface ForwardingBinder extends Binder {

  /** Returns the forwarded binder. */
  Binder binder();

  @Override
  default void bindInterceptor(
      Matcher<? super Class<?>> classMatcher,
      Matcher<? super Method> methodMatcher,
      MethodInterceptor... interceptors) {
    binder().bindInterceptor(classMatcher, methodMatcher, interceptors);
  }

  @Override
  default void bindScope(Class<? extends Annotation> annotationType, Scope scope) {
    binder().bindScope(annotationType, scope);
  }

  @Override
  default <T> LinkedBindingBuilder<T> bind(Key<T> key) {
    return binder().bind(key);
  }

  @Override
  default <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
    return binder().bind(typeLiteral);
  }

  @Override
  default <T> AnnotatedBindingBuilder<T> bind(Class<T> type) {
    return binder().bind(type);
  }

  @Override
  default AnnotatedConstantBindingBuilder bindConstant() {
    return binder().bindConstant();
  }

  @Override
  default <T> void requestInjection(TypeLiteral<T> type, T instance) {
    binder().requestInjection(type, instance);
  }

  @Override
  default void requestInjection(Object instance) {
    binder().requestInjection(instance);
  }

  @Override
  default void requestStaticInjection(Class<?>... types) {
    binder().requestStaticInjection(types);
  }

  @Override
  default void install(Module module) {
    binder().install(module);
  }

  @Override
  default Stage currentStage() {
    return binder().currentStage();
  }

  @Override
  default void addError(String message, Object... arguments) {
    binder().addError(message, arguments);
  }

  @Override
  default void addError(Throwable t) {
    binder().addError(t);
  }

  @Override
  default void addError(Message message) {
    binder().addError(message);
  }

  @Override
  default <T> Provider<T> getProvider(Key<T> key) {
    return binder().getProvider(key);
  }

  @Override
  default <T> Provider<T> getProvider(Dependency<T> dependency) {
    return binder().getProvider(dependency);
  }

  @Override
  default <T> Provider<T> getProvider(Class<T> type) {
    return binder().getProvider(type);
  }

  @Override
  default <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
    return binder().getMembersInjector(typeLiteral);
  }

  @Override
  default <T> MembersInjector<T> getMembersInjector(Class<T> type) {
    return binder().getMembersInjector(type);
  }

  @Override
  default void convertToTypes(
      Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
    binder().convertToTypes(typeMatcher, converter);
  }

  @Override
  default void bindListener(Matcher<? super TypeLiteral<?>> typeMatcher, TypeListener listener) {
    binder().bindListener(typeMatcher, listener);
  }

  @Override
  default void bindListener(
      Matcher<? super Binding<?>> bindingMatcher, ProvisionListener... listeners) {
    binder().bindListener(bindingMatcher, listeners);
  }

  @Override
  default Binder withSource(Object source) {
    return binder().withSource(source);
  }

  @Override
  default Binder skipSources(Class<?>... classesToSkip) {
    return binder().skipSources(classesToSkip);
  }

  @Override
  default PrivateBinder newPrivateBinder() {
    return binder().newPrivateBinder();
  }

  @Override
  default void requireExplicitBindings() {
    binder().requireExplicitBindings();
  }

  @Override
  default void disableCircularProxies() {
    binder().disableCircularProxies();
  }

  @Override
  default void requireAtInjectOnConstructors() {
    binder().requireAtInjectOnConstructors();
  }

  @Override
  default void requireExactBindingAnnotations() {
    binder().requireExactBindingAnnotations();
  }

  @Override
  default void scanModulesForAnnotatedMethods(ModuleAnnotatedMethodScanner scanner) {
    binder().scanModulesForAnnotatedMethods(scanner);
  }
}
