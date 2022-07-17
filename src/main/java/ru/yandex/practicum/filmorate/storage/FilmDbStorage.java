package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final String sqlAllFilms = """
            SELECT F.film_id, F.film_name, F.description, F.release_date, F.duration, F.rate, 
            F.mpa_id, M.mpa_name 
            FROM FILMS as F LEFT JOIN MPA as M ON F.mpa_id=M.mpa_id
            """;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO FILMS (film_name, description, release_date, duration, rate, mpa_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getRate());
            ps.setInt(6, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        addGenreFilms(film.getGenres(), film.getId());
        return film;
    }

    private void addGenreFilms(Collection<Genre> genres, Long filmId) {
        if (genres == null)
            return;

        for (Genre genre : genres) {
            String sql = "INSERT INTO GENRE_FILMS (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = jdbcTemplate.query(sqlAllFilms, (rs, rowNum) -> makeFilm(rs));
        for (Film film : films) {
            film = getGenreInFilm(film);
            film = getLikeInFilm(film);
        }

        return films;
    }

    @Override
    public Film put(Film film) {
        String sql = "UPDATE FILMS SET film_name=?, release_date=?, description=?, duration=?, rate=?, mpa_id=? " +
                "WHERE film_id=?";
        jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(), film.getDuration(),
                film.getRate(), film.getMpa().getId(), film.getId());
        updateGenreInFilm(film);
        return film;
    }

    private void updateGenreInFilm(Film film) {
        if (film.getGenres() == null)
            return;

        String sql = "DELETE FROM GENRE_FILMS WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        for (Genre genre : film.getGenres()) {
            sql = "INSERT INTO GENRE_FILMS (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    @Override
    public Film getFilm(long idFilm) {
        String sqlWhere = " WHERE film_id=?";
        Film film = jdbcTemplate.queryForObject(sqlAllFilms + sqlWhere,(rs, rowNum) -> makeFilm(rs),
                idFilm);

        film = getGenreInFilm(film);
        film = getLikeInFilm(film);
        return film;
    }

    @Override
    public Film getFilm(String nameFilm) {
        String sqlWhere = " WHERE film_name=?";
        Film film = jdbcTemplate.queryForObject(sqlAllFilms + sqlWhere,(rs, rowNum) -> makeFilm(rs),
                        nameFilm);

        film = getGenreInFilm(film);
        film = getLikeInFilm(film);
        return film;
    }

    private Film getGenreInFilm(Film film) {
        if (film == null)
            return null;

        String sql = """
                SELECT G.genre_id, G.genre_name 
                FROM FILMS as F 
                INNER JOIN GENRE_FILMS as GF ON F.film_id=GF.film_id 
                INNER JOIN GENRE as G ON GF.genre_id=G.genre_id 
                WHERE F.film_id=?""";
        Collection<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), new Object[]{film.getId()});
        film.setGenres(new LinkedHashSet<>(genres));

        return film;
    }

    private Film getLikeInFilm(Film film) {
        if (film == null)
            return null;
        String sql = "SELECT user_id FROM LIKE_FILMS WHERE film_id=?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), film.getId());
        film.setLikes(new HashSet<>(likes));
        return film;
    }

    @Override
    public boolean containsNameFilm(String nameFilm) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE film_name=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, nameFilm) > 0;
    }

    @Override
    public boolean containsIdFilm(long idFilm) {
        String sql = "SELECT COUNT(*) FROM FILMS WHERE film_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, idFilm) > 0;
    }

    @Override
    public void addLike(Film film, User user) {
        String sql = "INSERT INTO LIKE_FILMS (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sql = "DELETE FROM LIKE_FILMS WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        long id = rs.getLong("film_id");
        String film_name = rs.getString("film_name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int rate = rs.getInt("rate");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");

        return Film.builder()
                .id(id)
                .name(film_name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .rate(rate)
                .mpa(Mpa.builder().id(mpaId).name(mpaName).build())
                .build();
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("genre_name");
        return Genre.builder().id(id).name(name).build();
    }
}
