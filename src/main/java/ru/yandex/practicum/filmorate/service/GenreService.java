package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdInvalidException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(int genreId) {
        if (genreId < 1)
            throw new IdInvalidException("genreId: " + genreId + " is incorrect");
        return genreStorage.getGenreById(genreId);
    }

    public Collection<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }
}
