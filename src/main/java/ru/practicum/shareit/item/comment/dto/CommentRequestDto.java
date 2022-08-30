package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может состоять только из пробелов")
    @NotEmpty(message = "Комментарий не может быть пустым")
    private String text;
}
