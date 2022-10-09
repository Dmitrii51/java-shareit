package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.booking.dto.BookingMapper;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.dto.ItemMapper;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationBookingServiceTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;

    private User user1;
    private User user2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");
        User user3 = new User(3, "testUser3", "testUser3@email.ru");
        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, null);
        item1 = itemService.addItem(ItemMapper.toItemPostRequestDto(item1), user1.getId());

        booking1 = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        booking1 = bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking1), user2.getId());
        bookingService.approveBooking(booking1.getId(), true, user1.getId());

        booking2 = new Booking(2, LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5), item1, user3, BookingStatus.WAITING);
        booking2 = bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking2), user3.getId());
    }

    @Test
    @Transactional
    void addBookingTest() {
        booking1 = bookingService.addBooking(BookingMapper.toBookingRequestDto(booking1), user2.getId());
        Booking savedBooking = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.id = :id", Booking.class)
                .setParameter("id", booking1.getId()).getSingleResult();
        assertThat(savedBooking).isEqualTo(booking1);
    }

    @Test
    @Transactional
    void approveBookingTest() {
        booking2 = bookingService.approveBooking(booking2.getId(), false, user1.getId());
        Booking savedBooking = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.id = :id", Booking.class)
                .setParameter("id", booking2.getId()).getSingleResult();
        assertThat(savedBooking.getStatus()).isEqualTo(booking2.getStatus());
    }

    @Test
    @Transactional
    void getBookingTest() {
        booking2 = bookingService.getBooking(booking1.getId(), user2.getId());
        Booking savedBooking = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.id = :id", Booking.class)
                .setParameter("id", booking2.getId()).getSingleResult();
        assertThat(savedBooking).isEqualTo(booking2);
    }

    @Test
    @Transactional
    void getAllUserBookingListTest() {
        List<Booking> userBookingList = bookingService.getUserBookingList(
                "ALL", user2.getId(), 0, 5);
        List<Booking> savedUserBookingList = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.booker = ?1 " +
                                "ORDER BY b.start DESC", Booking.class)
                .setParameter(1, user2)
                .getResultList();
        assertThat(savedUserBookingList.size()).isEqualTo(1);
        assertThat(savedUserBookingList).isEqualTo(userBookingList);
    }

    @Test
    @Transactional
    void getAllOwnerBookingListTest() {
        List<Booking> ownerBookingList = bookingService.getOwnerBookingList(
                "ALL", user1.getId(), 0, 5);
        List<Booking> savedOwnerBookingList = entityManager.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.item.owner.id = ?1 " +
                                "ORDER BY b.start DESC", Booking.class)
                .setParameter(1, user1.getId())
                .getResultList();
        assertThat(savedOwnerBookingList.size()).isEqualTo(2);
        assertThat(savedOwnerBookingList).isEqualTo(ownerBookingList);
    }
}
