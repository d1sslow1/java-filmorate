package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserService userService;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public Film createFilm(Film film) {
        validate(film);
        validateMpa(film.getMpa().getId());
        if (film.getGenres() != null) {
            validateGenres(film.getGenres());
        }
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        getById(film.getId());
        validate(film);
        validateMpa(film.getMpa().getId());
        if (film.getGenres() != null) {
            validateGenres(film.getGenres());
        }
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден: " + id));
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId);

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        try {
            filmStorage.getJdbcTemplate().update(sql, filmId, userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Лайк уже существует");
        }
    }

    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId);

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int deleted = filmStorage.getJdbcTemplate().update(sql, filmId, userId);
        if (deleted == 0) {
            throw new NoSuchElementException("Лайк от пользователя " + userId + " не найден");
        }
    }

    public List<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name as mpa_name, m.description as mpa_description, " +
                "COUNT(l.user_id) as likes_count " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        List<Film> films = filmStorage.getJdbcTemplate().query(sql, filmStorage::mapRowToFilm, count);

        // Загружаем жанры для популярных фильмов
        films.forEach(film -> {
            Set<Genre> genres = filmStorage.loadGenres(film.getId());
            film.setGenres(genres);
        });

        return films;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Длительность фильма должна быть положительным числом");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private void validateMpa(int mpaId) {
        mpaDbStorage.getById(mpaId)
                .orElseThrow(() -> new IllegalArgumentException("Рейтинг MPA не найден: " + mpaId));
    }

    private void validateGenres(Set<Genre> genres) {
        for (Genre genre : genres) {
            genreDbStorage.getById(genre.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Жанр не найден: " + genre.getId()));
        }
    }
}