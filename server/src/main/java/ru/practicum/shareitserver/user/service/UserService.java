package ru.practicum.shareitserver.user.service;


import ru.practicum.shareitserver.user.model.User;

import java.util.List;

public interface UserService {

    User getUser(int id);

    List<User> getUserList();

    User addUser(User newUser);

    User updateUser(User user, int userId);

    void deleteUser(int id);
}
