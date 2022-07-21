package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sql = "SELECT genre_id, genre_name FROM GENRE WHERE genre_id=?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), new Object[]{genreId})
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sql = "SELECT genre_id, genre_name FROM GENRE";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("genre_name");
        return Genre.builder().id(id).name(name).build();
    }
}
