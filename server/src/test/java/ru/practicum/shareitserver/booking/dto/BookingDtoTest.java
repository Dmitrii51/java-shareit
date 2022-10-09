package ru.practicum.shareitserver.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    private final JacksonTester<BookingDto> jsonBookingDto;
    private final JacksonTester<BookingForItemDto> jsonBookingForItemDto;
    private final JacksonTester<BookingRequestDto> jsonBookingRequestDto;

    private BookingDto bookingDto;
    private BookingForItemDto bookingForItemDto;
    private BookingRequestDto bookingRequestDto;

    @Autowired
    BookingDtoTest(JacksonTester<BookingDto> jsonBookingDto,
                   JacksonTester<BookingForItemDto> jsonBookingForItemDto,
                   JacksonTester<BookingRequestDto> jsonBookingRequestDto) {
        this.jsonBookingDto = jsonBookingDto;
        this.jsonBookingForItemDto = jsonBookingForItemDto;
        this.jsonBookingRequestDto = jsonBookingRequestDto;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        User user2 = new User(2, "testUser2", "testUser2@email.ru");

        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now());

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, request1);

        Booking booking1 = new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);

        bookingDto = BookingMapper.toBookingDto(booking1);
        bookingForItemDto = BookingMapper.toBookingForItemDto(booking1);
        bookingRequestDto = BookingMapper.toBookingRequestDto(booking1);
    }

    @Test
    void bookingDtoJsonTest() throws IOException {
        JsonContent<BookingDto> json = jsonBookingDto.write(bookingDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDto.getItem().getId());
        assertThat(json).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(json).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingDto.getItem().getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingDto.getItem().getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.item.owner.id")
                .isEqualTo(bookingDto.getItem().getOwner().getId());
        assertThat(json).extractingJsonPathStringValue("$.item.owner.name")
                .isEqualTo(bookingDto.getItem().getOwner().getName());
        assertThat(json).extractingJsonPathStringValue("$.item.owner.email")
                .isEqualTo(bookingDto.getItem().getOwner().getEmail());
        assertThat(json).extractingJsonPathNumberValue("$.item.request.id")
                .isEqualTo(bookingDto.getItem().getRequest().getId());
        assertThat(json).extractingJsonPathStringValue("$.item.request.description")
                .isEqualTo(bookingDto.getItem().getRequest().getDescription());
        assertThat(json).extractingJsonPathNumberValue("$.item.request.requestor.id")
                .isEqualTo(bookingDto.getItem().getRequest().getRequestor().getId());
        assertThat(json).extractingJsonPathStringValue("$.item.request.requestor.name")
                .isEqualTo(bookingDto.getItem().getRequest().getRequestor().getName());
        assertThat(json).extractingJsonPathStringValue("$.item.request.requestor.email")
                .isEqualTo(bookingDto.getItem().getRequest().getRequestor().getEmail());
        assertThat(json).extractingJsonPathStringValue("$.item.request.created")
                .isEqualTo(bookingDto.getItem().getRequest().getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDto.getBooker().getId());
        assertThat(json).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingDto.getBooker().getName());
        assertThat(json).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingDto.getBooker().getEmail());
        assertThat(json).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingForItemDto.getStatus().toString());
    }

    @Test
    void bookingForItemDtoJsonTest() throws IOException {
        JsonContent<BookingForItemDto> json = jsonBookingForItemDto.write(bookingForItemDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingForItemDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingForItemDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingForItemDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingForItemDto.getBookerId());
        assertThat(json).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingForItemDto.getStatus().toString());
    }

    @Test
    void bookingRequestDtoJsonTest() throws IOException {
        JsonContent<BookingRequestDto> json = jsonBookingRequestDto.write(bookingRequestDto);

        assertThat(json).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingRequestDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingRequestDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingRequestDto.getItemId());
    }
}
