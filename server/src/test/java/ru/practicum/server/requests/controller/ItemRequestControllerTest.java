package ru.practicum.server.requests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.dto.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.controller.ItemRequestController;
import ru.practicum.server.request.dto.ItemRequestDtoForRequest;
import ru.practicum.server.request.dto.ItemRequestMapper;
import ru.practicum.server.request.dto.ItemRequestWithUsersResponseDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private Item item1;
    private Item item2;


    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");

        itemRequest1 = new ItemRequest(
                1, "Test request1 of Item1", user1, LocalDateTime.now().minusDays(1));
        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, itemRequest1);

        itemRequest2 = new ItemRequest(
                2, "Test request2 of Item2", user2, LocalDateTime.now().minusDays(2));
        item2 = new Item(
                2, "testItem2", "Item2 for test", false, user2, itemRequest2);
    }

    @Test
    void getItemRequestEndpointTest() throws Exception {
        ItemRequestWithUsersResponseDto itemRequestDto1 = ItemRequestMapper.toItemRequestWithUsersResponseDto(
                itemRequest1, List.of(ItemMapper.toItemDto(item1)));
        when(itemRequestService.getItemRequestWithResponse(itemRequest1.getId(), user1.getId()))
                .thenReturn(itemRequestDto1);
        mockMvc.perform(get("/requests/{requestId}", itemRequest1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(itemRequestDto1)));
    }

    @Test
    void getUserItemRequestListEndpointTest() throws Exception {
        List<ItemRequestWithUsersResponseDto> itemRequestDtoList = List.of(
                ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        itemRequest1, List.of(ItemMapper.toItemDto(item1))));
        when(itemRequestService.getUserItemRequestList(user1.getId()))
                .thenReturn(itemRequestDtoList);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(itemRequestDtoList)));
    }

    @Test
    void getPageableItemRequestListEndpointTest() throws Exception {
        List<ItemRequestWithUsersResponseDto> itemRequestDtoList = List.of(
                ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        itemRequest2, List.of(ItemMapper.toItemDto(item2))));
        when(itemRequestService.getPageableItemRequestList(user1.getId(), 0, Optional.of(1)))
                .thenReturn(itemRequestDtoList);
        mockMvc.perform(get("/requests/all")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(itemRequestDtoList)));
    }

    @Test
    void createItemRequestEndpointTest() throws Exception {
        ItemRequestDtoForRequest newItemRequest = ItemRequestMapper.toItemRequestDtoForRequest(itemRequest1);
        when(itemRequestService.addItemRequest(newItemRequest, user1.getId()))
                .thenReturn(itemRequest1);
        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId())
                        .content(mapper.writeValueAsString(newItemRequest)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(newItemRequest)));
    }
}
