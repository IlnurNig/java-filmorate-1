package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;


public interface FilmStorage {
    public Film create(Film film);
    public Collection<Film> findAll();
    public Film put(Film film);
    public Film getFilm(long idFilm);
    public Film getFilm(String nameFilm);
    public boolean containsNameFilm(String nameFilm);
    public boolean containsIdFilm(long idFilm);

}
