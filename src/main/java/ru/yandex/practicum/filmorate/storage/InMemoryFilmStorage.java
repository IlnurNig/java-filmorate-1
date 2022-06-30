package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmIsMissingException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;


@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmsKeyId = new HashMap<>();
    private final Map<String, Film> filmsKeyName = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(++Film.countFilm);
        return put(film);
    }

    @Override
    public Collection<Film> findAll() {
        return new HashSet<>(filmsKeyId.values());
    }

    @Override
    public Film put(Film film) {
        filmsKeyId.put(film.getId(), film);
        filmsKeyName.put(film.getName(), film);
        return film;
    }

    @Override
    public Film getFilm(long idFilm) {
        Film film = filmsKeyId.get(idFilm);
        if (film == null)
            throw new FilmIsMissingException("the film with id=" + idFilm + " is missing");
        return film;
    }

    @Override
    public Film getFilm(String nameFilm) {
        Film film = filmsKeyName.get(nameFilm);
        if (film == null)
            throw new FilmIsMissingException("the film with name " + nameFilm + " is missing");
        return film;
    }

    @Override
    public boolean containsNameFilm(String nameFilm) {
        return filmsKeyName.containsKey(nameFilm);
    }

    @Override
    public boolean containsIdFilm(long idFilm) {
        return filmsKeyId.containsKey(idFilm);
    }

}
