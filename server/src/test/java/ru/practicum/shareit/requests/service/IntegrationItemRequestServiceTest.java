package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithUsersResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;

    private ItemRequest request1;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user1 = userService.addUser(user1);
        user2 = new User(2, "testUser2", "testUser2@email.ru");
        user2 = userService.addUser(user2);

        request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now().minusDays(2));
        ItemRequestDtoForRequest itemRequest1DtoForRequest = ItemRequestMapper.toItemRequestDtoForRequest(request1);
        request1 = itemRequestService.addItemRequest(itemRequest1DtoForRequest, user2.getId());

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, request1);
        itemService.addItem(ItemMapper.toItemPostRequestDto(item1), user1.getId());
    }

    @Test
    @Transactional
    void getItemRequestTest() {
        ItemRequest savedItemRequest = entityManager.createQuery(
                        "SELECT i FROM ItemRequest i " +
                                "WHERE i.id = :id", ItemRequest.class)
                .setParameter("id", request1.getId()).getSingleResult();
        assertThat(savedItemRequest).isEqualTo(request1);
    }

    @Test
    @Transactional
    void getItemRequestWithResponseTest() {
        ItemRequestWithUsersResponseDto userItemRequest = itemRequestService.getItemRequestWithResponse(
                request1.getId(), user2.getId());

        ItemRequest savedUserItemRequest = entityManager.createQuery(
                        "SELECT i FROM ItemRequest i " +
                                "WHERE i.requestor.id = :id", ItemRequest.class)
                .setParameter("id", user2.getId()).getSingleResult();

        List<Item> savedItemForUserRequest = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.request.id = :id", Item.class)
                .setParameter("id", request1.getId()).getResultList();

        ItemRequestWithUsersResponseDto savedItemRequestWithResponse = ItemRequestMapper
                .toItemRequestWithUsersResponseDto(savedUserItemRequest,
                        List.of(ItemMapper.toItemDto(savedItemForUserRequest.get(0))));

        assertThat(savedItemRequestWithResponse).isEqualTo(userItemRequest);
    }

    @Test
    @Transactional
    void getUserItemRequestListTest() {
        List<ItemRequestWithUsersResponseDto> userItemRequests = itemRequestService.getUserItemRequestList(
                user2.getId());

        List<ItemRequest> savedUserItemRequests = entityManager.createQuery(
                        "SELECT i FROM ItemRequest i " +
                                "WHERE i.requestor.id = :id", ItemRequest.class)
                .setParameter("id", user2.getId()).getResultList();

        List<Item> savedItemForUserRequest = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.request.id = :id", Item.class)
                .setParameter("id", request1.getId()).getResultList();

        List<ItemRequestWithUsersResponseDto> savedUserRequestsWithResponse = List.of(
                ItemRequestMapper.toItemRequestWithUsersResponseDto(
                        savedUserItemRequests.get(0),
                        List.of(ItemMapper.toItemDto(savedItemForUserRequest.get(0)))));

        assertThat(userItemRequests.size()).isEqualTo(savedUserItemRequests.size());
        assertThat(savedUserRequestsWithResponse).isEqualTo(userItemRequests);
    }
}
