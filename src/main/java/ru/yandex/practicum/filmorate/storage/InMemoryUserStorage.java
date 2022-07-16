package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserIsMissingException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersKeyId = new HashMap<>();
    private final Map<String, User> usersKeyEmail = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return new HashSet<>(usersKeyId.values());
    }

    @Override
    public User create(User user) {
        user.setId(++User.countUser);
        return put(user);
    }

    @Override
    public User put(User user) {
        usersKeyId.put(user.getId(), user);
        usersKeyEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User getUser(long idUser) {
        User user = usersKeyId.get(idUser);
        if (user == null)
            throw new UserIsMissingException("the user with id=" + idUser + " is missing");
        return user;
    }

    @Override
    public boolean containsEmailUser(String email) {
        return usersKeyEmail.containsKey(email);
    }

    @Override
    public boolean containsIdUser(long idUser) {
        return usersKeyId.containsKey(idUser);
    }

    @Override
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
    }

    @Override
    public Collection<User> getFriends(User user) {
        return user.getFriends()
                .stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
    }

}
