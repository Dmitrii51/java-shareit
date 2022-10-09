package ru.practicum.gateway.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.client.UserClient;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.validators.OnCreate;


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
    public ResponseEntity<Object> createUser(@Validated(OnCreate.class) @RequestBody User newUser) {
        return userClient.addUser(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable int userId, @RequestBody User user) {
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        return userClient.deleteUser(userId);
    }
}
