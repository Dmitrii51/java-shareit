package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUserList();

    User addUser(User newUser);

    User updateUser(User user, int userId);

    void deleteUser(int id);
}
