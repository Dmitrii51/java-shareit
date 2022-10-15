package ru.practicum.server.user.service;


import ru.practicum.server.user.model.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUserList();

    User addUser(User newUser);

    User updateUser(User user, int userId);

    void deleteUser(int id);
}
