package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User getUser(int id);

    List<User> getUserList();

    User addUser(User user);

    User updateUser(User user, int userId);

    void deleteUser(int id);

    void deleteUserList();
}
