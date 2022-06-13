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

    public User getUserById(long id){
        if(id < 1){
            throw new IdInvalidException("id User: " + id + " is incorrect");
        }
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        if(userStorage.containsEmailUser(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }

        String checkUser = validationUser(user);
        if(!(checkUser.isBlank())){
            throw new ValidationUserException(checkUser);
        }

        if(user.getName() == null ||user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User putUser(User user) {
        String checkUser = validationUser(user);
        if(!(checkUser.isBlank())){
            throw new ValidationUserException(checkUser);
        }

        if(user.getId() < 1){
            throw new IdInvalidException("id user: " + user.getId() + " is incorrect");
        }

        if(!(userStorage.containsIdUser(user.getId()))){
            throw new ValidationUserException("the film with id=" + user.getId() + " is missing");
        }

        return userStorage.put(user);
    }

    public void addFriend(long idUser, long idFriend){
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.getFriends().add(idFriend);
        friend.getFriends().add(idUser);
    }

    public void deleteFriend(long idUser, long idFriend){
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.getFriends().remove(idFriend);
        friend.getFriends().remove(idUser);
    }

    public Collection<User> getFriends(long idUser){
        User user = userStorage.getUser(idUser);
        return user.getFriends()
                .stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long idUser, long idFriend){
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        Collection<Long> idFriends = new HashSet<>(user.getFriends());
        idFriends.retainAll(friend.getFriends());
        return idFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    private String validationUser(User user){
        List<String> result = new ArrayList<>();
        if(Pattern.compile("\\s").matcher(user.getLogin()).find()){
            result.add("Логин не может содержать пробелы");
        }
        if(LocalDate.now().isBefore(user.getBirthday())){
            result.add("Дата рождения не может быть в будущем");
        }
        return String.join(", ", result);
    }

}
