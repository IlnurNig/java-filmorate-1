package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO USERS (email, login, name, birthday) VALUES(?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User put(User user) {
        jdbcTemplate.update("UPDATE USERS SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUser(long idUser) {
        String sql = "SELECT * FROM USERS WHERE user_id=?";
        User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), idUser);
        return addFriendForUser(user);
    }

    public User getUser(String login) {
        String sql = "SELECT * FROM USERS WHERE login=?";
        User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), login);
        return addFriendForUser(user);
    }

    private User addFriendForUser(User user) {
        if (user == null)
            return null;

        String sql = "SELECT user_2 FROM FRIENDS WHERE friend_status=true AND user_1=" + user.getId();
        Collection<Long> friends = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_2"));
        user.setFriends(new HashSet<>(friends));

        return user;
    }

    @Override
    public boolean containsEmailUser(String email) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE email=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, email) > 0;
    }

    @Override
    public boolean containsIdUser(long idUser) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE user_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, idUser) > 0;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sql = "INSERT INTO FRIENDS (user_1, user_2, friend_status) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), friend.getId(), true);
        jdbcTemplate.update(sql, friend.getId(), user.getId(), false);
    }

    @Override
    public Collection<User> getFriends(User user) {
        StringBuilder sql = new StringBuilder()
                .append(" SELECT USER_FRIEND.user_id, U.email, U.login, U.name, U.birthday ")
                .append(" FROM(")
                .append(" SELECT F.user_2 as user_id")
                .append(" FROM FRIENDS as F")
                .append(" INNER JOIN USERS as U ON F.user_1=U.user_id")
                .append(" WHERE F.user_1=")
                .append(user.getId())
                .append(" AND F.friend_status=true")
                .append(") as USER_FRIEND")
                .append(" INNER JOIN USERS as U ON USER_FRIEND.user_id=U.user_id");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public void deleteFriend(long idUser, long idFriend) {
        String sql = "DELETE FROM FRIENDS WHERE user_1=? AND user_2=?";
        jdbcTemplate.update(sql, idUser, idFriend);
    }


    private User makeUser(ResultSet rs) throws SQLException {

        long id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        // Получаем дату и конвертируем её из sql.Date в time.LocalDate
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return User.builder()
                .id(id)
                .birthday(birthday)
                .email(email)
                .login(login)
                .name(name)
                .build();
    }
}
