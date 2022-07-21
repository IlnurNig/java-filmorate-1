package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Component
@Primary
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Mpa getMpaById(int mpaId) {
        String sql = "SELECT mpa_id, mpa_name FROM MPA WHERE mpa_id=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), new Object[]{mpaId})
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id, mpa_name FROM MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("mpa_name");
        return Mpa.builder().id(id).name(name).build();
    }
}
