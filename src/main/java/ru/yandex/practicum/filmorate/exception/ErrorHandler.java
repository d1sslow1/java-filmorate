package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationException(final ValidationException e) {
    log.warn("Ошибка валидации: {}", e.getMessage());
    Map<String, String> response = new HashMap<>();
    response.put("error", e.getMessage());
    return response;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
    log.warn("Ошибка валидации данных: {}", errorMessage);

    Map<String, String> response = new HashMap<>();
    response.put("error", errorMessage);
    return response;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFoundException(final NotFoundException e) {
    log.warn("Объект не найден: {}", e.getMessage());
    Map<String, String> response = new HashMap<>();
    response.put("error", e.getMessage());
    return response;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleOtherExceptions(final Exception e) {
    log.error("Произошла непредвиденная ошибка: {}", e.getMessage(), e);
    Map<String, String> response = new HashMap<>();
    response.put("error", "Произошла непредвиденная ошибка");
    return response;
  }
}
