package user;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class UserTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        assertTrue(application.isRunning());
    }
}
