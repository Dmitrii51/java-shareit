package ru.practicum.server.item.comment.dto;


import ru.practicum.server.item.comment.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment fromCommentRequestDto(
            CommentRequestDto commentRequestDto, Item commentedItem, User author) {
        return new Comment(
                null,
                commentRequestDto.getText(),
                commentedItem,
                author,
                LocalDateTime.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static CommentRequestDto toCommentRequestDto(Comment comment) {
        return new CommentRequestDto(
                comment.getText()
        );
    }
}
