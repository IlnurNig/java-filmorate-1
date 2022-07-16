package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User put(User user);

    User getUser(long idUser);

    boolean containsEmailUser(String email);

    boolean containsIdUser(long idUser);

    void addFriend(User user, User friend);

    Collection<User> getFriends(User user);

    void deleteFriend(User user, User friend);
}
