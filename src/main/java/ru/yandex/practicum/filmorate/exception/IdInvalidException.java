package ru.yandex.practicum.filmorate.exception;


public class IdInvalidException extends RuntimeException {
    public IdInvalidException(String message) {
        super(message);
    }
}
