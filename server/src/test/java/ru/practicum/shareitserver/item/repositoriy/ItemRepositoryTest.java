package ru.practicum.shareitserver.item.repositoriy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;


@DataJpaTest
class ItemRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    private Item item1;
    private ItemRequest request1;

    @Autowired
    public ItemRepositoryTest(UserRepository userRepository,
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
        User user3 = userRepository.save(new User(3, "testUser3", "testUser3@email.ru"));

        request1 = requestRepository.save(new ItemRequest(
                1, "Test request1 of Item1", user3, LocalDateTime.now().minusDays(2)));
        ItemRequest request2 = requestRepository.save(new ItemRequest(
                1, "Test request2 of Item2", user1, LocalDateTime.now().minusDays(2)));

        item1 = itemRepository.save(new Item(
                1, "testItem1", "Item1 for test", true, user1, request1));
        Item item2 = itemRepository.save(new Item(
                2, "testItem2", "Item2 for test", false, user2, request2));
    }

    @Test
    void searchTest() {
        List<Item> searchResultList = itemRepository.search(PageRequest.of(0, 5), "test");
        Assertions.assertEquals(1, searchResultList.size(),
                "Несоответствие количества вещей, найденных по запросу");
        Assertions.assertEquals(item1, searchResultList.get(0),
                "Несоотвествие вещи, найденной по запросу");
    }

    @Test
    void getItemsFotItemRequestTest() {
        List<Item> itemRequestList = itemRepository.getItemsFotItemRequest(request1);
        Assertions.assertEquals(1, itemRequestList.size(),
                "Несоответствие количества вещей, созданных по запросу");
        Assertions.assertEquals(item1, itemRequestList.get(0),
                "Несоотвествие вещи, созданной по запросу");
    }
}
