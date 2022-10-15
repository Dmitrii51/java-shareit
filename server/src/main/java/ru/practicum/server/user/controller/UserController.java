package ru.practicum.server.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return UserMapper.toUserDto(userService.getUser(userId));
    }

    @GetMapping
    public List<UserDto> getUserList() {
        return userService.getUserList().stream().map((UserMapper::toUserDto)).collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestBody User newUser) {
        return UserMapper.toUserDto(userService.addUser(newUser));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @RequestBody User user) {
        return UserMapper.toUserDto(userService.updateUser(user, userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }
}
