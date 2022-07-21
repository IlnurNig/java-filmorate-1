package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Mpa getMpaById(int mpaId);

    Collection<Mpa> getAllMpa();
}
