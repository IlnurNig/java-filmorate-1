package ru.yandex.practicum.filmorate.exception;


public class FilmIsMissingException extends RuntimeException {
    public FilmIsMissingException(String message) {
        super(message);
    }
}
