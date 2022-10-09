package ru.practicum.gateway.user.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.user.model.User;

public interface UserClient {

    ResponseEntity<Object> getUser(int userId);

    ResponseEntity<Object> getUserList();

    ResponseEntity<Object> addUser(User newUser);

    ResponseEntity<Object> updateUser(User user, int userId);

    ResponseEntity<Object> deleteUser(int userId);
}
