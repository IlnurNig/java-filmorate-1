package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(long id) {
        validationIdUser(id);
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        if (userStorage.containsEmailUser(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }

        String checkUser = validationUser(user);
        if (!(checkUser.isBlank())) {
            throw new ValidationUserException(checkUser);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User putUser(User user) {
        String checkUser = validationUser(user);
        if (!(checkUser.isBlank())) {
            throw new ValidationUserException(checkUser);
        }
        validationIdUser(user.getId());

        return userStorage.put(user);
    }

    public void addFriend(long idUser, long idFriend) {
        User user = userStorage.getUser(idUser);
        if (user == null) {
            throw new UserIsMissingException("the user with id=" + idUser + " is missing");
        }

        User friend = userStorage.getUser(idFriend);
        if (friend == null) {
            throw new UserIsMissingException("the user with id=" + idFriend + " is missing");
        }

        userStorage.addFriend(user, friend);
    }

    public void deleteFriend(long idUser, long idFriend) {
        User user = userStorage.getUser(idUser);
        if (user == null)
            return;

        User friend = userStorage.getUser(idFriend);
        if (friend == null)
            return;

        userStorage.deleteFriend(user, friend);
    }

    public Collection<User> getFriends(long idUser) {
        User user = userStorage.getUser(idUser);
        return userStorage.getFriends(user);
    }

    public Collection<User> getCommonFriends(long idUser, long idFriend) {
        User user = userStorage.getUser(idUser);
        if (user == null)
            return new ArrayList<>();

        User friend = userStorage.getUser(idFriend);
        if (friend == null)
            return new ArrayList<>();

        if (user.getFriends() == null) {
            return new ArrayList<>();
        }

        if (friend.getFriends() == null) {
            return new ArrayList<>();
        }

        Collection<Long> idFriends = new HashSet<>(user.getFriends());
        idFriends.retainAll(friend.getFriends());
        return idFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    private void validationIdUser(long idUser) {
        if (idUser < 1)
            throw new IdInvalidException("idUser: " + idUser + " is incorrect");

        if (!(userStorage.containsIdUser(idUser)))
            throw new ValidationUserException("the user with id=" + idUser + " is missing");
    }

    private String validationUser(User user) {
        List<String> result = new ArrayList<>();
        if (Pattern.compile("\\s").matcher(user.getLogin()).find()) {
            result.add("Логин не может содержать пробелы");
        }
        if (LocalDate.now().isBefore(user.getBirthday())) {
            result.add("Дата рождения не может быть в будущем");
        }
        return String.join(", ", result);
    }

}
