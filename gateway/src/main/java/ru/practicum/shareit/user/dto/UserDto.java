package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validators.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class UserDto {

    private Integer id;

    @NotBlank(message = "Имя пользователя не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Имя пользователя не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotNull(message = "Электронная почта не может быть пустой", groups = OnCreate.class)
    @Email(message = "Электронная почта должна содержать символ @", groups = OnCreate.class)
    private String email;
}
