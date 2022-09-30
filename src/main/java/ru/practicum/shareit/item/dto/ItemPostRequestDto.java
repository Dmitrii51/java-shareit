package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validators.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemPostRequestDto {

    @NotBlank(message = "Название вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Название вещи не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Описание вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Описание вещи не может быть пустым", groups = OnCreate.class)
    private String description;

    @NotNull(message = "Статус доступности обязателен для заполнения", groups = OnCreate.class)
    private Boolean available;

    @Positive
    private Integer requestId;
}
