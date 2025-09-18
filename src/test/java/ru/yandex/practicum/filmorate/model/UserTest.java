package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithValidData() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertNull(user.getId());
        assertEquals("test@mail.com", user.getEmail());
        assertEquals("testlogin", user.getLogin());
        assertEquals("Test User", user.getName());
        assertTrue(user.getFriends().isEmpty());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        UserService userService = new UserService(new InMemoryUserStorage());
        User createdUser = userService.createUser(user);

        assertEquals("testlogin", createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        UserService userService = new UserService(new InMemoryUserStorage());

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldAddFriend() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");

        user.getFriends().add(1);
        user.getFriends().add(2);

        assertEquals(2, user.getFriends().size());
        assertTrue(user.getFriends().contains(1));
        assertTrue(user.getFriends().contains(2));
    }
}