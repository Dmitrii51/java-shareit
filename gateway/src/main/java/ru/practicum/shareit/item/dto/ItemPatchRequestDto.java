package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validators.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemPatchRequestDto {

    private Integer id;

    @NotBlank(message = "Название вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Название вещи не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Описание вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Описание вещи не может быть пустым", groups = OnCreate.class)
    private String description;

    @NotNull(message = "Статус доступности обязателен для заполнения", groups = OnCreate.class)
    private Boolean available;

    private User owner;

    private ItemRequest request;
}
