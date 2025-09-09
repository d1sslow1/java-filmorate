package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    String description;

    @NotNull(message = "Дата релиза обязательна")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    int duration;
}