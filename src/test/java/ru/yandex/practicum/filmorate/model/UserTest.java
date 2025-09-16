package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUserShouldPass() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertEquals(0, validator.validate(user).size());
    }

    @Test
    void invalidEmailShouldFail() {
        User user = new User();
        user.setEmail("invalid");
        user.setLogin("login");

        assertTrue(validator.validate(user).size() > 0);
    }

    @Test
    void blankLoginShouldFail() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("");

        assertTrue(validator.validate(user).size() > 0);
    }

    @Test
    void loginWithSpacesShouldFail() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login with spaces");

        assertTrue(validator.validate(user).size() > 0);
    }

    @Test
    void futureBirthdayShouldFail() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertTrue(validator.validate(user).size() > 0);
    }
}