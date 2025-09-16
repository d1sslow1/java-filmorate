package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class FilmTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validFilmShouldPass() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertEquals(0, validator.validate(film).size());
    }

    @Test
    void blankNameShouldFail() {
        Film film = new Film();
        film.setName("");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void nullReleaseDateShouldFail() {
        Film film = new Film();
        film.setName("Film");
        film.setReleaseDate(null);
        film.setDuration(120);

        assertEquals(1, validator.validate(film).size());
    }

    @Test
    void negativeDurationShouldFail() {
        Film film = new Film();
        film.setName("Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        assertEquals(1, validator.validate(film).size());
    }
}
