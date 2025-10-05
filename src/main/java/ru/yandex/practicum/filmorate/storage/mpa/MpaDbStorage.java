package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id"; // Исправлено на mpa_ratings
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public Optional<Mpa> getById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?"; // Исправлено на mpa_ratings
        List<Mpa> mpaList = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        return mpaList.isEmpty() ? Optional.empty() : Optional.of(mpaList.get(0));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("name"));
        mpa.setDescription(rs.getString("description"));
        return mpa;
    }
}