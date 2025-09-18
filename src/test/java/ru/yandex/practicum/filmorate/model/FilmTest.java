package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    @Test
    void shouldCreateFilmWithValidData() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertNull(film.getId());
        assertEquals("Test Film", film.getName());
        assertEquals("Test Description", film.getDescription());
        assertEquals(120, film.getDuration());
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        FilmService filmService = new FilmService(new InMemoryFilmStorage(), null);

        assertThrows(ValidationException.class, () -> filmService.createFilm(film));
    }

    @Test
    void shouldAddLikeToFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDuration(120);

        film.getLikes().add(1);
        film.getLikes().add(2);

        assertEquals(2, film.getLikes().size());
        assertTrue(film.getLikes().contains(1));
        assertTrue(film.getLikes().contains(2));
    }
}