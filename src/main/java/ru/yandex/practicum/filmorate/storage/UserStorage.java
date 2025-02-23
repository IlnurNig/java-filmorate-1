package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;


public interface UserStorage {
    public Collection<User> findAll();
    public User create(User user);
    public User put(User user);
    public User getUser(long idUser);
    public boolean containsEmailUser(String email);
    public boolean containsIdUser(long idUser);
}
