package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validators.OnCreate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    @GetMapping
    public List<UserDto> getUserList() {
        return userStorage.getUserList().stream().map((UserMapper::toUserDto)).collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody User newUser) {
        return UserMapper.toUserDto(userStorage.addUser(newUser));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @RequestBody User user) {
        return UserMapper.toUserDto(userStorage.updateUser(user, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userStorage.deleteUser(userId);
    }
}
