package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface FilmStorage {
    Film create(Film film);

    Collection<Film> findAll();

    Film put(Film film);

    Film getFilm(long idFilm);

    Film getFilm(String nameFilm);

    boolean containsNameFilm(String nameFilm);

    boolean containsIdFilm(long idFilm);

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

}
