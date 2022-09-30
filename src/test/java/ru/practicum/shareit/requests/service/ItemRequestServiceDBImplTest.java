package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositoriy.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDtoForRequest;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceDBImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceDBImpl itemRequestServiceDB;
    private ItemRequest request1;
    private Item item1;
    private User user3;

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        user3 = new User(3, "testUser3", "testUser3@email.ru");
        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, null);
        request1 = new ItemRequest(
                1, "Test request1 of Item1", user3, LocalDateTime.now().minusDays(2));
    }

    @Test
    void addItemRequestTest() {
        ItemRequestDtoForRequest itemRequestDtoForRequest = ItemRequestMapper.toItemRequestDtoForRequest(request1);
        when(userService.getUser(user3.getId())).thenReturn(user3);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request1);
        assertThat(itemRequestServiceDB.addItemRequest(itemRequestDtoForRequest, user3.getId())).isEqualTo(request1);
    }

    @Test
    void getUserItemRequestListTest() {
        when(userService.getUser(user3.getId())).thenReturn(user3);
        when(itemRequestRepository.getUserItemRequestList(any(User.class))).thenReturn(List.of(request1));
        when(itemRepository.getItemsFotItemRequest(any(ItemRequest.class))).thenReturn(List.of(item1));
        assertThat(itemRequestServiceDB.getUserItemRequestList(user3.getId())).isEqualTo(List.of(
                ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        request1, List.of(ItemMapper.toItemDto(item1)))));
    }

    @Test
    void getPageableItemRequestListTest() {
        when(userService.getUser(user3.getId())).thenReturn(user3);
        when(itemRequestRepository.getPageableItemRequestList(any(PageRequest.class), any(User.class)))
                .thenReturn(List.of(request1));
        when(itemRepository.getItemsFotItemRequest(any(ItemRequest.class))).thenReturn(List.of(item1));
        assertThat(itemRequestServiceDB.getPageableItemRequestList(user3.getId(), 0, Optional.of(1)))
                .isEqualTo(List.of(ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        request1, List.of(ItemMapper.toItemDto(item1)))));
    }

    @Test
    void getPageableItemRequestListWithNegativeSizeTest() {
        when(userService.getUser(user3.getId())).thenReturn(user3);
        assertThatThrownBy(() -> itemRequestServiceDB.getPageableItemRequestList(
                user3.getId(), 0, Optional.of(-1))).isInstanceOf(ValidationException.class);
    }

    @Test
    void getItemRequestTest() {
        when(itemRequestRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(request1));
        assertThat(itemRequestServiceDB.getItemRequest(any(Integer.class))).isEqualTo(request1);
    }

    @Test
    void getNonExistentItemRequestTest() {
        when(itemRequestRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemRequestServiceDB.getItemRequest(any(Integer.class)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getItemRequestWithResponseTest() {
        when(userService.getUser(user3.getId())).thenReturn(user3);
        when(itemRequestRepository.findById(any(Integer.class))).thenReturn(Optional.ofNullable(request1));
        when(itemRepository.getItemsFotItemRequest(any(ItemRequest.class))).thenReturn(List.of(item1));
        assertThat(itemRequestServiceDB.getItemRequestWithResponse(request1.getId(), user3.getId()))
                .isEqualTo(ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        request1, List.of(ItemMapper.toItemDto(item1))));
    }
}
