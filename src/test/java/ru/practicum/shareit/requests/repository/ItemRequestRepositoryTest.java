package ru.practicum.shareit.requests.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositoriy.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@DataJpaTest
class ItemRequestRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    private User user3;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @Autowired
    public ItemRequestRepositoryTest(UserRepository userRepository,
                                     ItemRepository itemRepository,
                                     ItemRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = userRepository.save(new User(1, "testUser1", "testUser1@email.ru"));
        User user2 = userRepository.save(new User(2, "testUser2", "testUser2@email.ru"));
        user3 = userRepository.save(new User(3, "testUser3", "testUser3@email.ru"));

        request1 = requestRepository.save(new ItemRequest(
                1, "Test request1 of Item1", user3, LocalDateTime.now().minusDays(2)));
        request2 = requestRepository.save(new ItemRequest(
                2, "Test request2 of Item2", user3, LocalDateTime.now().minusDays(4)));
        request3 = requestRepository.save(new ItemRequest(
                3, "Test request3 of Item2", user1, LocalDateTime.now().minusDays(3)));

        Item item1 = itemRepository.save(new Item(
                1, "testItem1", "Item1 for test", true, user1, request1));
        Item item2 = itemRepository.save(new Item(
                2, "testItem2", "Item2 for test", false, user2, request2));
    }

    @Test
    void getUserItemRequestListTest() {
        List<ItemRequest> userItemRequestList = requestRepository.getUserItemRequestList(user3);
        Assertions.assertEquals(2, userItemRequestList.size(),
                "Несоответствие количества запросов пользователя");
        List<ItemRequest> user3RequestList = Arrays.asList(request1, request2);
        Assertions.assertEquals(user3RequestList, userItemRequestList,
                "Несоотвествие запросов пользователя");
    }

    @Test
    void getPageableItemRequestListTest() {
        List<ItemRequest> userPageableItemRequestList = requestRepository.getPageableItemRequestList(
                PageRequest.of(0, 2), user3);
        System.out.println(userPageableItemRequestList);
        Assertions.assertEquals(1, userPageableItemRequestList.size(),
                "Несоответствие количества запросов пользователя");
        Assertions.assertEquals(request3, userPageableItemRequestList.get(0),
                "Несоотвествие запросов пользователя");
    }
}
