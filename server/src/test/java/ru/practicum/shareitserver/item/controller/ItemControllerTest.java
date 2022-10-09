package ru.practicum.shareitserver.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.booking.dto.BookingMapper;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.comment.dto.CommentMapper;
import ru.practicum.shareitserver.item.comment.dto.CommentRequestDto;
import ru.practicum.shareitserver.item.comment.model.Comment;
import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.item.dto.ItemMapper;
import ru.practicum.shareitserver.item.dto.ItemPostRequestDto;
import ru.practicum.shareitserver.item.dto.ItemWithBookingDto;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

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
                LocalDateTime.now().plusDays(6), item1, user3, BookingStatus.APPROVED);

        comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5));
    }

    @Test
    void getItemRequestEndpointTest() throws Exception {
        ItemWithBookingDto itemWithBookingDto1 = ItemMapper.toItemWithBookingDto(
                item1, BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2), List.of(CommentMapper.toCommentDto(comment1)));
        when(itemService.getItemWithBooking(itemWithBookingDto1.getId(), user1.getId()))
                .thenReturn(itemWithBookingDto1);
        mockMvc.perform(get("/items/{itemId}", itemRequest1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(itemWithBookingDto1)));
    }

    @Test
    void getUserItemListEndpointTest() throws Exception {
        List<ItemWithBookingDto> userItemList = List.of(ItemMapper.toItemWithBookingDto(
                item1, BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2), List.of(CommentMapper.toCommentDto(comment1))));
        when(itemService.getUserItemList(user1.getId(), 0, 1)).thenReturn(userItemList);
        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(userItemList)));
    }

    @Test
    void getItemListWithRequestedSearchParametersEndpointTest() throws Exception {
        List<ItemDto> userItemList = List.of(ItemMapper.toItemDto(item1), ItemMapper.toItemDto(item2));
        when(itemService.getItemListWithRequestedSearchParameters(
                "Test", 0, 5)).thenReturn(List.of(item1, item2));
        mockMvc.perform(get("/items/search")
                        .param("text", "Test")
                        .param("from", "0")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(userItemList)));
    }

    @Test
    void createItemEndpointTest() throws Exception {
        ItemPostRequestDto newItem = ItemMapper.toItemPostRequestDto(item1);
        when(itemService.addItem(newItem, user1.getId())).thenReturn(item1);
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .content(mapper.writeValueAsString(newItem)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        ItemMapper.toItemDto(item1))));
    }

    @Test
    void createCommentEndpointTest() throws Exception {
        CommentRequestDto newComment = CommentMapper.toCommentRequestDto(comment1);
        when(itemService.addComment(newComment, item1.getId(), user1.getId())).thenReturn(comment1);
        mockMvc.perform(post("/items/{itemId}/comment", item1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .content(mapper.writeValueAsString(newComment)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        CommentMapper.toCommentDto(comment1))));
    }

    @Test
    void updateItemEndpointTest() throws Exception {
        when(itemService.updateItem(item1, item1.getId(), user1.getId())).thenReturn(item1);
        mockMvc.perform(patch("/items/{itemId}", item1.getId()).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .content(mapper.writeValueAsString(item1)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(
                        ItemMapper.toItemDto(item1))));
    }

    @Test
    void deleteItemEndpointTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", item1.getId()))
                .andExpectAll(status().isOk());
    }
}
