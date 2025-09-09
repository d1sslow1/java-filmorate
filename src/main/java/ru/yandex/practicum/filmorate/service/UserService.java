package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public List<User> getAllUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        // Валидация теперь автоматически через аннотации в модели
        user.setId(nextId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        // Валидация теперь автоматически через аннотации в модели
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }
}