package ru.practicum.server.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может состоять только из пробелов")
    @NotEmpty(message = "Комментарий не может быть пустым")
    private String text;
}
