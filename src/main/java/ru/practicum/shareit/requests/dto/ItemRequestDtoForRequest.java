package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDtoForRequest {
    @NotBlank(message = "Запрос вещи не может состоять только из пробелов")
    @NotEmpty(message = "Запрос вещи не может быть пустым")
    private String description;
}
