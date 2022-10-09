package ru.practicum.shareitserver.item.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareitserver.item.comment.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    private final JacksonTester<CommentDto> jsonCommentDto;
    private final JacksonTester<CommentRequestDto> jsonCommentRequestDto;

    private CommentDto commentDto;
    private CommentRequestDto commentRequestDto;

    @Autowired
    CommentDtoTest(JacksonTester<CommentDto> jsonCommentDto,
                   JacksonTester<CommentRequestDto> jsonCommentRequestDto) {
        this.jsonCommentDto = jsonCommentDto;
        this.jsonCommentRequestDto = jsonCommentRequestDto;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        User user2 = new User(2, "testUser2", "testUser2@email.ru");

        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now());

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, request1);

        Comment comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5)
        );

        commentDto = CommentMapper.toCommentDto(comment1);
        commentRequestDto = CommentMapper.toCommentRequestDto(comment1);
    }

    @Test
    void commentDtoTest() throws IOException {
        JsonContent<CommentDto> json = jsonCommentDto.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(json).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void commentRequestDtoTest() throws IOException {
        JsonContent<CommentRequestDto> json = jsonCommentRequestDto.write(commentRequestDto);

        assertThat(json).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
    }
}
