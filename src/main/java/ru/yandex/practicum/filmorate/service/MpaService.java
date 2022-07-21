package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdInvalidException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaById(int mpaId) {
        if (mpaId < 1)
            throw new IdInvalidException("mpaId: " + mpaId + " is incorrect");

        return mpaStorage.getMpaById(mpaId);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}
