package ru.yandex.practicum.filmorate.exception;


public class UserIsMissingException extends RuntimeException {
    public UserIsMissingException(String message) {
        super(message);
    }
}
