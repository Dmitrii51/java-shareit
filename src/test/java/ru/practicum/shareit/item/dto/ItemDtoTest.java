package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    private final JacksonTester<ItemDto> jsonItemDto;
    private final JacksonTester<ItemPostRequestDto> jsonItemPostRequestDto;
    private final JacksonTester<ItemWithBookingDto> jsonItemWithBookingDto;

    private ItemDto itemDto;
    private ItemPostRequestDto itemPostRequestDto;
    private ItemWithBookingDto itemWithBookingDto;

    @Autowired
    ItemDtoTest(JacksonTester<ItemDto> jsonItemDto,
                JacksonTester<ItemPostRequestDto> jsonItemPostRequestDto,
                JacksonTester<ItemWithBookingDto> jsonItemWithBookingDto) {
        this.jsonItemDto = jsonItemDto;
        this.jsonItemPostRequestDto = jsonItemPostRequestDto;
        this.jsonItemWithBookingDto = jsonItemWithBookingDto;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        User user2 = new User(2, "testUser2", "testUser2@email.ru");
        User user3 = new User(3, "testUser3", "testUser3@email.ru");

        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now());

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, request1);

        Booking booking1 = new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(3, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), item1, user3, BookingStatus.APPROVED);

        Comment comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5)
        );

        itemDto = ItemMapper.toItemDto(item1);
        itemPostRequestDto = ItemMapper.toItemPostRequestDto(item1);
        itemWithBookingDto = ItemMapper.toItemWithBookingDto(item1, BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2), List.of(CommentMapper.toCommentDto(comment1)));
    }

    @Test
    void itemDtoTest() throws IOException {
        JsonContent<ItemDto> json = jsonItemDto.write(itemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId());
    }

    @Test
    void itemPostRequestDtoTest() throws IOException {
        JsonContent<ItemPostRequestDto> json = jsonItemPostRequestDto.write(itemPostRequestDto);

        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemPostRequestDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemPostRequestDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemPostRequestDto.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemPostRequestDto.getRequestId());
    }

    @Test
    void itemWithBookingDtoTest() throws IOException {
        JsonContent<ItemWithBookingDto> json = jsonItemWithBookingDto.write(itemWithBookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemWithBookingDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemWithBookingDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemWithBookingDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemWithBookingDto.getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemWithBookingDto.getLastBooking().getId());
        assertThat(json).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(itemWithBookingDto.getLastBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(itemWithBookingDto.getLastBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemWithBookingDto.getLastBooking().getBookerId());
        assertThat(json).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(itemWithBookingDto.getLastBooking().getStatus().toString());
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemWithBookingDto.getNextBooking().getId());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(itemWithBookingDto.getNextBooking().getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(itemWithBookingDto.getNextBooking().getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemWithBookingDto.getNextBooking().getBookerId());
        assertThat(json).extractingJsonPathStringValue("$.nextBooking.status")
                .isEqualTo(itemWithBookingDto.getNextBooking().getStatus().toString());
        assertThat(json).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemWithBookingDto.getComments().get(0).getId());
        assertThat(json).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemWithBookingDto.getComments().get(0).getText());
        assertThat(json).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemWithBookingDto.getComments().get(0).getAuthorName());
        assertThat(json).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemWithBookingDto.getComments().get(0).getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
