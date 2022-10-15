package ru.practicum.server.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.booking.dto.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.item.comment.dto.CommentMapper;
import ru.practicum.server.item.comment.model.Comment;
import ru.practicum.server.item.dto.ItemMapper;
import ru.practicum.server.item.dto.ItemWithBookingDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.dto.ItemRequestDtoForRequest;
import ru.practicum.server.request.dto.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.service.ItemRequestService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final EntityManager entityManager;

    private User user1;
    private Item item1;
    private Booking booking1;
    private Booking booking2;
    private Comment comment1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        User user2 = new User(2, "testUser2", "testUser2@email.ru");
        User user3 = new User(3, "testUser3", "testUser3@email.ru");
        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now().minusDays(2));
        ItemRequestDtoForRequest itemRequest1DtoForRequest = ItemRequestMapper.toItemRequestDtoForRequest(request1);
        ItemRequest savedItemRequest = itemRequestService.addItemRequest(itemRequest1DtoForRequest, user2.getId());
        request1.setId(savedItemRequest.getId());

        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1,
                itemRequestService.getItemRequest(request1.getId()));
        item1 = itemService.addItem(ItemMapper.toItemPostRequestDto(item1), user1.getId());


        booking1 = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        booking2 = new Booking(2, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), item1, user3, BookingStatus.APPROVED);
        booking1 = bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking1), user2.getId());
        booking2 = bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking2), user3.getId());
        bookingService.approveBooking(booking1.getId(), true, user1.getId());
        bookingService.approveBooking(booking2.getId(), true, user1.getId());

        comment1 = new Comment(1, "testComment", item1, user2, LocalDateTime.now());
        comment1 = itemService.addComment(
                CommentMapper.toCommentRequestDto(comment1), item1.getId(), user2.getId());
    }

    @Test
    @Transactional
    void getItemTest() {
        Item savedItem = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.id = :id", Item.class)
                .setParameter("id", item1.getId()).getSingleResult();
        assertThat(savedItem).isEqualTo(item1);
    }

    @Test
    @Transactional
    void getItemWithBookingTest() {
        ItemWithBookingDto savedItemWithBookingDto = itemService.getItemWithBooking(item1.getId(), user1.getId());
        Item savedItem = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.id = :id", Item.class)
                .setParameter("id", item1.getId()).getSingleResult();

        booking1 = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.item = ?1 " +
                                "AND b.start < ?2 " +
                                "ORDER BY b.start DESC", Booking.class)
                .setParameter(1, item1)
                .setParameter(2, LocalDateTime.now()).getSingleResult();

        booking2 = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.item = ?1 " +
                                "AND b.start > ?2 " +
                                "ORDER BY b.start DESC", Booking.class)
                .setParameter(1, item1)
                .setParameter(2, LocalDateTime.now()).getSingleResult();
        Comment comment = entityManager.createQuery("SELECT c FROM Comment c " +
                "WHERE c.id = :id", Comment.class).setParameter("id", comment1.getId()).getSingleResult();
        ItemWithBookingDto createdItemWithBookingDto = ItemMapper.toItemWithBookingDto(
                savedItem, BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2), List.of(CommentMapper.toCommentDto(comment)));
        assertThat(createdItemWithBookingDto).isEqualTo(savedItemWithBookingDto);
    }

    @Test
    @Transactional
    void updateItemTest() {
        item1.setName("Item1");
        itemService.updateItem(item1, item1.getId(), user1.getId());
        Item updatedItem = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.id = :id", Item.class)
                .setParameter("id", item1.getId()).getSingleResult();
        assertThat(updatedItem).isEqualTo(item1);
    }

    @Test
    @Transactional
    void getUserItemListTest() {
        List<ItemWithBookingDto> savedUserItemList = itemService.getUserItemList(user1.getId(), 0, 5);
        List<ItemWithBookingDto> userItemList = List.of(ItemMapper.toItemWithBookingDto(
                item1, BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2), List.of(CommentMapper.toCommentDto(comment1))));
        assertThat(savedUserItemList).isEqualTo(userItemList);
    }

    @Test
    @Transactional
    void getItemListWithRequestedSearchParametersTest() {
        List<Item> itemList = itemService.getItemListWithRequestedSearchParameters("Test", 0, 5);
        assertThat(itemList).isEqualTo(List.of(item1));
    }

    @Test
    @Transactional
    void addCommentTest() {
        Comment savedComment = entityManager.createQuery(
                "SELECT c FROM Comment c " +
                        "WHERE c.id = :id", Comment.class).setParameter("id", comment1.getId()).getSingleResult();
        assertThat(savedComment.getId()).isEqualTo(comment1.getId());
        assertThat(savedComment.getText()).isEqualTo(comment1.getText());
        assertThat(savedComment.getItem()).isEqualTo(comment1.getItem());
        assertThat(savedComment.getAuthor()).isEqualTo(comment1.getAuthor());
    }

    @Test
    @Transactional
    void deleteItemTest() {
        itemService.deleteItem(item1.getId());
        TypedQuery<Item> query = entityManager.createQuery(
                        "SELECT i FROM Item i " +
                                "WHERE i.id = :id", Item.class)
                .setParameter("id", item1.getId());
        assertThat(query.getResultList().size()).isEqualTo(0);
    }
}
