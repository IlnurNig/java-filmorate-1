package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdInvalidException;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(long id) {
        validationIdFilm(id);
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        if (filmStorage.containsNameFilm(film.getName())) {
            return filmStorage.getFilm(film.getName());
        }
        String checkFilm = validationFilm(film);
        if (!checkFilm.isBlank()) {
            throw new ValidationFilmException(checkFilm);
        }
        return filmStorage.create(film);
    }

    public Film put(Film film) {
        String checkFilm = validationFilm(film);
        if (!(checkFilm.isBlank())) {
            throw new ValidationFilmException(checkFilm);
        }
        validationIdFilm(film.getId());

        return filmStorage.put(film);
    }

    public void addLike(long idFilm, long idUser) {
        validationIdUser(idUser);
        validationIdFilm(idFilm);
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        filmStorage.addLike(film, user);
    }

    public void deleteLike(long idFilm, long idUser) {
        validationIdUser(idUser);
        validationIdFilm(idFilm);
        Film film = filmStorage.getFilm(idFilm);
        User user = userStorage.getUser(idUser);
        filmStorage.deleteLike(film, user);
    }

    public Collection<Film> getPopularFilm(Optional<Integer> count) {
        return filmStorage.findAll()
                .stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count.orElseGet(() -> 10))
                .collect(Collectors.toList());
    }

    private void validationIdUser(long idUser) {
        if (idUser < 1)
            throw new IdInvalidException("idUser: " + idUser + " is incorrect");

        if (!(userStorage.containsIdUser(idUser)))
            throw new ValidationUserException("the user with id=" + idUser + " is missing");
    }

    private void validationIdFilm(long idFilm) {
        if (idFilm < 1)
            throw new IdInvalidException("idFilm: " + idFilm + " is incorrect");

        if (!filmStorage.containsIdFilm(idFilm))
            throw new ValidationFilmException("the film with id=" + idFilm + " is missing");
    }

    private String validationFilm(Film film) {
        List<String> result = new ArrayList<>();
        if (film.getName() == null || film.getName().isBlank()) {
            result.add("название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            result.add("максимальная длина описания более 200 символов");
        }
        LocalDate BirthdayFilms = LocalDate.parse("28 12 1895",
                DateTimeFormatter.ofPattern("dd MM yyyy"));
        if (BirthdayFilms.isAfter(film.getReleaseDate())) {
            result.add("дата релиза раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            result.add("продолжительность фильма должна быть положительной");
        }
        return String.join(", ", result);
    }

}
