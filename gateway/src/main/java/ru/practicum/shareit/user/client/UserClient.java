package ru.practicum.shareit.user.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserClient {

    ResponseEntity<Object> getUser(int userId);

    ResponseEntity<Object> getUserList();

    ResponseEntity<Object> addUser(UserDto newUser);

    ResponseEntity<Object> updateUser(UserDto user, int userId);

    ResponseEntity<Object> deleteUser(int userId);
}
