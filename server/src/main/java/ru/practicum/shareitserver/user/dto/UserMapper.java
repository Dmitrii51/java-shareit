package ru.practicum.shareitserver.user.dto;


import ru.practicum.shareitserver.user.model.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
