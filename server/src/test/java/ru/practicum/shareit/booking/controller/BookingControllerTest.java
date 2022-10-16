package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user1;
    private User user2;
    private User user3;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Comment comment1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");
        user3 = new User(3, "testUser3", "testUser3@email.ru");

        itemRequest1 = new ItemRequest(
                1, "Test request1 of Item1", user1, LocalDateTime.now().minusDays(1));
        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, itemRequest1);

        itemRequest2 = new ItemRequest(
                2, "Test request2 of Item2", user2, LocalDateTime.now().minusDays(2));
        item2 = new Item(
                2, "testItem2", "Item2 for test", false, user2, itemRequest2);

        booking1 = new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        booking2 = new Booking(3, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), item1, user3, BookingStatus.WAITING);

        comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5));
    }

    @Test
    void createBookingEndpointTest() throws Exception {
        BookingRequestDto newBooking = BookingMapper.toBookingRequestDto(booking2);
        when(bookingService.addBooking(newBooking, user3.getId())).thenReturn(booking2);
        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user3.getId())
                        .content(mapper.writeValueAsString(newBooking)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        BookingMapper.toBookingDto(booking2))));
    }

    @Test
    void updateBookingEndpointTest() throws Exception {
        when(bookingService.approveBooking(booking2.getId(), true, user1.getId())).thenReturn(booking2);
        mockMvc.perform(patch("/bookings/{bookingId}", booking2.getId())
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        BookingMapper.toBookingDto(booking2))));
    }

    @Test
    void getBookingEndpointTest() throws Exception {
        when(bookingService.getBooking(booking1.getId(), user1.getId()))
                .thenReturn(booking1);
        mockMvc.perform(get("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(BookingMapper.toBookingDto(booking1))));
    }

    @Test
    void getUserBookingListEndpointTest() throws Exception {
        List<Booking> userBookingList = List.of(booking2);
        when(bookingService.getUserBookingList("ALL", 3, 0, 1)).thenReturn(userBookingList);
        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", user3.getId()))
                .andExpectAll(status().isOk(),
                        content().json(mapper.writeValueAsString(List.of(BookingMapper.toBookingDto(booking2)))));
    }

    @Test
    void getOwnerBookingListEndpointTest() throws Exception {
        List<Booking> userBookingList = List.of(booking1, booking2);
        when(bookingService.getOwnerBookingList("ALL", 1, 0, 5)).thenReturn(userBookingList);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        userBookingList.stream()
                                .map(BookingMapper::toBookingDto)
                                .collect(Collectors.toList()))));
    }
}
