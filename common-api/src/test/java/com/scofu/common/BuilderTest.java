package com.scofu.common;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;

/** Tests builder. */
public class BuilderTest {

  @Test
  public void test() {
    final var person =
        Person.person().name().first("Foo").last("Bar").endName().age(100).mood("Happy").build();
    assertNotNull(person);
    assertNotNull(person.name());
    assertEquals("Foo", person.name().first());
    assertEquals("Bar", person.name().last());
    assertEquals(100, person.age());
    assertEquals("Happy", person.mood().orElseThrow());
  }

  @Test
  public void testRequired() {
    assertThrowsExactly(NullPointerException.class, () -> Person.person().build());
    assertThrowsExactly(NullPointerException.class, () -> Person.person().name().endName().build());
    assertThrowsExactly(
        NullPointerException.class, () -> Person.person().name().first("First").endName().build());
    assertThrowsExactly(
        NullPointerException.class,
        () -> Person.person().name().first("First").last("Last").endName().build());
    assertThrowsExactly(
        IllegalArgumentException.class,
        () -> Person.person().name().first("First").last("Last").endName().age(-1).build());
  }

  @Test
  public void testOptional() {
    final var person = Person.person().name().first("Foo").last("Bar").endName().age(100).build();
    assertEquals(Optional.empty(), person.mood());
  }

  @Test
  public void testEdit() {
    final var person = Person.person().name().first("Foo").last("Bar").endName().age(100).build();
    final var moodyPerson = person.edit().mood("Moody").build();
    assertNotEquals(person, moodyPerson);
    assertEquals(person.name(), moodyPerson.name());
    assertEquals(person.age(), moodyPerson.age());
    assertEquals("Moody", moodyPerson.mood().orElseThrow());
  }

  private record Name(String first, String last) {
    public static NameBuilder<Void> name() {
      return new NameBuilder<>();
    }

    public NameBuilder<Void> edit() {
      return new NameBuilder<>(this);
    }
  }

  private record Person(Name name, Integer age, Optional<String> mood) {
    public static PersonBuilder<Void> person() {
      return new PersonBuilder<>();
    }

    public PersonBuilder<Void> edit() {
      return new PersonBuilder<>(this);
    }
  }

  private static class NameBuilder<R> extends AbstractBuilder<Name, R, NameBuilder<R>> {

    private String first;
    private String last;

    public NameBuilder(@Nullable Name from, @Nullable R parent, @Nullable Consumer<Name> consumer) {
      super(from, parent, consumer);
    }

    public NameBuilder(@Nullable R parent, @Nullable Consumer<Name> consumer) {
      super(parent, consumer);
    }

    public NameBuilder(Name from) {
      super(from);
    }

    public NameBuilder() {}

    @Override
    protected void initializeFrom(Name name) {
      this.first = name.first;
      this.last = name.last;
    }

    public NameBuilder<R> first(String first) {
      this.first = first;
      return this;
    }

    public NameBuilder<R> last(String last) {
      this.last = last;
      return this;
    }

    public R endName() {
      return end();
    }

    @Override
    public Name build() {
      return new Name(require(first, "first"), require(last, "last"));
    }
  }

  private static class PersonBuilder<R> extends AbstractBuilder<Person, R, PersonBuilder<R>> {

    private Name name;
    private Integer age;
    private String mood;

    public PersonBuilder(
        @Nullable Person from, @Nullable R parent, @Nullable Consumer<Person> consumer) {
      super(from, parent, consumer);
    }

    public PersonBuilder(@Nullable R parent, @Nullable Consumer<Person> consumer) {
      super(parent, consumer);
    }

    public PersonBuilder(Person from) {
      super(from);
    }

    public PersonBuilder() {}

    @Override
    protected void initializeFrom(Person person) {
      this.name = person.name;
      this.age = person.age;
      this.mood = person.mood.orElse(null);
    }

    public NameBuilder<PersonBuilder<R>> name() {
      return new NameBuilder<>(name, this, name -> this.name = name);
    }

    public PersonBuilder<R> age(Integer age) {
      checkArgument(age >= 0, "age must be at least 0");
      this.age = age;
      return this;
    }

    public PersonBuilder<R> mood(String mood) {
      this.mood = mood;
      return this;
    }

    public R endPerson() {
      return end();
    }

    @Override
    public Person build() {
      return new Person(require(name, "name"), require(age, "age"), optional(mood));
    }
  }
}
