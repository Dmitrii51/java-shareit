package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicateException;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> userList;
    private int uniqueId;

    public InMemoryUserStorage() {
        userList = new HashMap<>();
    }

    @Override
    public User getUser(int id) {
        if (userList.containsKey(id)) {
            return userList.get(id);
        }
        log.warn("Запрос данных о несуществующем пользователе с id - {}", id);
        throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", id));
    }

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(userList.values());
    }

    @Override
    public User addUser(User newUser) {
        validateNewUser(newUser);
        uniqueId += 1;
        newUser.setId(uniqueId);
        userList.put(newUser.getId(), newUser);
        log.info("Добавление нового пользователя c id {}", newUser.getId());
        return newUser;
    }

    @Override
    public User updateUser(User user, int userId) {
        validateUpdatedUser(user, userId);
        User savedUser = userList.get(userId);
        user.setId(savedUser.getId());
        if (user.getEmail() == null) {
            user.setEmail(savedUser.getEmail());
        }
        if (user.getName() == null) {
            user.setName(savedUser.getName());
        }
        userList.put(userId, user);
        log.info("Изменение информации о пользователе с id {}", user.getId());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (userList.containsKey(id)) {
            userList.remove(id);
            log.info("Удаление пользователя с id {}", id);
        } else {
            log.warn("Попытка удаления несуществующего пользователя с id - {}", id);
            throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", id));
        }
    }

    @Override
    public void deleteUserList() {
        userList.clear();
        log.info("Удаление списка пользователей");
    }

    private boolean isUserEmailDuplicate(User newUser) {
        for (User user : userList.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void validateNewUser(User newUser) {
        if (newUser.getId() != 0) {
            log.warn("Добавление пользователя с некорректным id - {}", newUser);
            throw new ValidationException("Ошибка добавления пользователя. " +
                    "Id пользователя должен быть равен 0");
        }
        if (isUserEmailDuplicate(newUser)) {
            log.warn("Добавление пользователя с некорректным адресом почты - {}", newUser);
            throw new DuplicateException(String.format("Пользователь с почтой = %s уже существует",
                    newUser.getEmail()));
        }
    }

    private void validateUpdatedUser(User user, int userId) {
        if (!userList.containsKey(userId)) {
            log.warn("Попытка обновления несуществующего пользователя - {}", user);
            throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        if (isUserEmailDuplicate(user)) {
            log.warn("Попытка изменения адреса почты пользователя на уже существующий - {}", user);
            throw new DuplicateException(String.format("Пользователь с почтой = %s уже существует",
                    user.getEmail()));
        }
    }
}
