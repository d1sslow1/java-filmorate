package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("birthday", user.getBirthday());

        Number generatedId = simpleJdbcInsert.executeAndReturnKey(parameters);
        user.setId(generatedId.intValue());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }

        User user = users.get(0);
        user.setFriends(loadFriends(id));

        return Optional.of(user);
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);

        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        loadFriendsForUsers(userMap);

        return users;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private Set<Integer> loadFriends(int userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ? AND status = 'confirmed'";
        List<Integer> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        log.debug("Загружено друзей для пользователя {}: {}", userId, friends.size());
        return new HashSet<>(friends);
    }

    private void loadFriendsForUsers(Map<Integer, User> userMap) {
        if (userMap.isEmpty()) return;

        String inClause = userMap.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String sql = "SELECT user_id, friend_id FROM friendships WHERE user_id IN (" + inClause + ") AND status = 'confirmed'";

        jdbcTemplate.query(sql, rs -> {
            int userId = rs.getInt("user_id");
            User user = userMap.get(userId);
            if (user != null) {
                user.getFriends().add(rs.getInt("friend_id"));
            }
        });
    }

    // Методы для работы с дружбой
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'confirmed')";
        jdbcTemplate.update(sql, userId, friendId);
        log.debug("Добавлена дружба: {} -> {}", userId, friendId);
    }

    public void confirmFriend(int userId, int friendId) {
        String sql = "UPDATE friendships SET status = 'confirmed' WHERE user_id = ? AND friend_id = ?";
        int updated = jdbcTemplate.update(sql, userId, friendId);
        log.debug("Подтверждена дружба: {} -> {} (обновлено строк: {})", userId, friendId, updated);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        int deleted = jdbcTemplate.update(sql, userId, friendId);
        log.debug("Удалена дружба: {} -> {} (удалено строк: {})", userId, friendId, deleted);
    }

    public List<Integer> getFriendIds(int userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ? AND status = 'confirmed'";
        List<Integer> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        log.debug("Найдено ID друзей для пользователя {}: {}", userId, friends);
        return friends;
    }

    public List<Integer> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT f1.friend_id FROM friendships f1 " +
                "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 'confirmed' AND f2.status = 'confirmed'";
        List<Integer> commonFriends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, otherId);
        log.debug("Общие друзья пользователей {} и {}: {}", userId, otherId, commonFriends);
        return commonFriends;
    }
}