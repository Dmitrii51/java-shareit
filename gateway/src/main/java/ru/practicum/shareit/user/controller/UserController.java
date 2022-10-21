package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validators.OnCreate;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable int userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserList() {
        return userClient.getUserList();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(OnCreate.class) @RequestBody UserDto newUser) {
        return userClient.addUser(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId, @RequestBody UserDto user) {
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        return userClient.deleteUser(userId);
    }
}
