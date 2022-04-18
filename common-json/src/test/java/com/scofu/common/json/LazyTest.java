package com.scofu.common.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.inject.Inject;
import com.google.inject.Stage;
import com.scofu.app.Service;
import com.scofu.app.bootstrap.BootstrapModule;
import com.scofu.common.json.lazy.Lazy;
import com.scofu.common.json.lazy.LazyFactory;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Tests {@link Lazy}.
 */
@TestInstance(Lifecycle.PER_CLASS)
public class LazyTest extends Service {

  @Inject
  private LazyFactory lazyFactory;
  @Inject
  private Json json;

  @Override
  protected void configure() {
    install(new BootstrapModule(getClass().getClassLoader()));
  }

  @BeforeAll
  public void setup() {
    load(Stage.PRODUCTION, this);
  }

  @Test
  public void testBindings() {
    interface User extends Lazy {

      Optional<String> name();

      void setName(String name);

      String password();

      void setPassword(String password);

      int coins();

      void setCoins(int coins);
    }

    final var user = lazyFactory.create(User.class);
    assertTrue(user.name().isEmpty());
    assertNull(user.password());
    assertEquals(0, user.coins());
    assertEquals("{}", user.toString());
    assertEquals(user, json.fromString(User.class, user.toString()));

    user.setName("Name");
    user.setPassword("Password");
    user.setCoins(100);

    assertEquals("Name", user.name().orElseThrow());
    assertEquals("Password", user.password());
    assertEquals(100, user.coins());
    assertEquals("{\"name\":\"Name\",\"password\":\"Password\",\"coins\":100}", user.toString());
    assertNotEquals("{\"name\":\"NamE\",\"password\":\"Password\",\"coins\":100}", user.toString());
    assertEquals(user, json.fromString(User.class, user.toString()));
    assertNotEquals(user,
        json.fromString(User.class, "{\"name\":\"NamE\",\"password\":\"Password\",\"coins\":100}"));

    user.setName(null);
    assertTrue(user.name().isEmpty());
    assertEquals("{\"password\":\"Password\",\"coins\":100}", user.toString());
  }

  @Test
  public void testDefaults() {
    final var user = lazyFactory.create(PublicUser.class);
    user.setName("nAmE");
    assertEquals("Name", user.fancyName());
  }

  /**
   * Has to be public to support default methods.
   */
  public interface PublicUser extends Lazy {

    String name();

    void setName(String name);

    default String fancyName() {
      final var substring = name().substring(1);
      return Character.toUpperCase(name().charAt(0)) + substring.toLowerCase(Locale.ROOT);
    }
  }
}