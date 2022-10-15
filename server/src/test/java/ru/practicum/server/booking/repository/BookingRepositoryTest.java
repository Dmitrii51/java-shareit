package ru.practicum.server.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repositoriy.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@DataJpaTest
class BookingRepositoryTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Booking booking5;

    @Autowired
    public BookingRepositoryTest(UserRepository userRepository,
                                 ItemRepository itemRepository,
                                 BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1, "testUser1", "testUser1@email.ru"));
        user2 = userRepository.save(new User(2, "testUser2", "testUser2@email.ru"));
        user3 = userRepository.save(new User(3, "testUser3", "testUser3@email.ru"));

        item1 = itemRepository.save(new Item(
                1, "testItem1", "Item1 for test", true, user1, null));
        Item item2 = itemRepository.save(new Item(
                2, "testItem2", "Item2 for test", true, user2, null));

        Booking booking1 = bookingRepository.save(new Booking(1, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item2, user1, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED));
        booking3 = bookingRepository.save(new Booking(3, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4), item1, user3, BookingStatus.WAITING));
        booking4 = bookingRepository.save(new Booking(4, LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), item2, user3, BookingStatus.APPROVED));
        booking5 = bookingRepository.save(new Booking(5, LocalDateTime.now().minusDays(15),
                LocalDateTime.now().minusDays(12), item1, user3, BookingStatus.APPROVED));
    }

    @Test
    void getAllBookerBookingsTest() {
        List<Booking> bookerBookingList = bookingRepository.getAllBookerBookings(
                PageRequest.of(0, 5), user3);
        Assertions.assertEquals(3, bookerBookingList.size(),
                "Несоответствие количества бронирований пользователя");
        Assertions.assertEquals(bookerBookingList.get(0).getBooker(), user3,
                "Несоответствие пользователя, запросившего бронирование");
        List<Booking> user3BookingList = Arrays.asList(booking3, booking4, booking5);
        Assertions.assertEquals(user3BookingList, bookerBookingList,
                "Нарушен порядок сортировки бронирований пользователя");
    }

    @Test
    void getBookerFutureBookingsTest() {
        List<Booking> bookerFutureBookingList = bookingRepository.getBookerFutureBookings(
                PageRequest.of(0, 5), user3, LocalDateTime.now());
        Assertions.assertEquals(1, bookerFutureBookingList.size(),
                "Несоответствие количества будущих бронирований пользователя");
        Assertions.assertEquals(booking3, bookerFutureBookingList.get(0),
                "Несоответствие будущего бронирования пользователя");
    }

    @Test
    void getBookerPastBookingsTest() {
        List<Booking> bookerPastBookingList = bookingRepository.getBookerPastBookings(
                PageRequest.of(0, 5), user3, LocalDateTime.now());
        Assertions.assertEquals(1, bookerPastBookingList.size(),
                "Несоответствие количества завершенных бронирований пользователя");
        Assertions.assertEquals(booking5, bookerPastBookingList.get(0),
                "Несоответствие завершенного бронирования пользователя");
    }

    @Test
    void getBookerCurrentBookingsTest() {
        List<Booking> bookerCurrentBookingList = bookingRepository.getBookerCurrentBookings(
                PageRequest.of(0, 5), user3, LocalDateTime.now(), LocalDateTime.now());
        Assertions.assertEquals(1, bookerCurrentBookingList.size(),
                "Несоответствие количества текущих бронирований пользователя");
        Assertions.assertEquals(booking4, bookerCurrentBookingList.get(0),
                "Несоответствие текущего бронирования пользователя");
    }

    @Test
    void getBookerBookingsWithCertainStatusTest() {
        List<Booking> bookerWaitingBookingList = bookingRepository.getBookerBookingsWithCertainStatus(
                PageRequest.of(0, 5), user3, BookingStatus.WAITING);
        Assertions.assertEquals(1, bookerWaitingBookingList.size(),
                "Несоответствие количества бронирований пользователя ожидающих подтверждения");
        Assertions.assertEquals(booking3, bookerWaitingBookingList.get(0),
                "Несоответствие бронирования пользователя ожидающего подтверждения");
    }

    @Test
    void getAllOwnerBookingsTest() {
        List<Booking> ownerBookingList = bookingRepository.getAllOwnerBookings(
                PageRequest.of(0, 5), user1.getId());
        Assertions.assertEquals(3, ownerBookingList.size(),
                "Несоответствие количества бронирований владельца");
        Assertions.assertEquals(ownerBookingList.get(0).getItem().getOwner(), user1,
                "Несоответствие владельца вешей");
        List<Booking> user3BookingList = Arrays.asList(booking3, booking2, booking5);
        Assertions.assertEquals(user3BookingList, ownerBookingList,
                "Нарушен порядок сортировки бронирований владельца вещей");
    }

    @Test
    void getOwnerFutureBookingsExcludingCertainStatusTest() {
        List<Booking> ownerFutureBookingList = bookingRepository.getOwnerFutureBookingsExcludingCertainStatus(
                PageRequest.of(0, 5), user1.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
        Assertions.assertEquals(1, ownerFutureBookingList.size(),
                "Несоответствие количества будущих бронирований вещей владельца");
        Assertions.assertEquals(booking3, ownerFutureBookingList.get(0),
                "Несоответствие будущего бронирования вещи владельца");
    }

    @Test
    void getOwnerPastBookingsExcludingCertainStatusTest() {
        List<Booking> ownerPastBookingList = bookingRepository.getOwnerPastBookingsExcludingCertainStatus(
                PageRequest.of(0, 5), user1.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
        Assertions.assertEquals(2, ownerPastBookingList.size(),
                "Несоответствие количества завершенных бронирований вещей владельца");
        List<Booking> user1BookingList = Arrays.asList(booking2, booking5);
        Assertions.assertEquals(user1BookingList, ownerPastBookingList,
                "Нарушен порядок сортировки завершенных бронирований вещей владельца");
    }

    @Test
    void getOwnerCurrentBookingsTest() {
        List<Booking> bookerCurrentBookingList = bookingRepository.getOwnerCurrentBookings(
                PageRequest.of(0, 5), user2.getId(), LocalDateTime.now(), LocalDateTime.now());
        Assertions.assertEquals(1, bookerCurrentBookingList.size(),
                "Несоответствие количества текущих бронирований вешей владельца");
        Assertions.assertEquals(booking4, bookerCurrentBookingList.get(0),
                "Несоответствие текущего бронирования вещи владельца");
    }

    @Test
    void getOwnerBookingsWithCertainStatusTest() {
        List<Booking> ownerWaitingBookingList = bookingRepository.getOwnerBookingsWithCertainStatus(
                PageRequest.of(0, 5), user1.getId(), BookingStatus.WAITING);
        Assertions.assertEquals(1, ownerWaitingBookingList.size(),
                "Несоответствие количества бронирований вещи владельца, ожидающих подтверждения");
        Assertions.assertEquals(booking3, ownerWaitingBookingList.get(0),
                "Несоответствие бронирования вещи владельца ожидающего подтверждения");
    }

    @Test
    void getItemNextBookingTest() {
        List<Booking> itemNextBookingList = bookingRepository.getItemNextBookings(
                PageRequest.of(0, 1), item1, LocalDateTime.now(), BookingStatus.APPROVED);
        Assertions.assertEquals(booking3, itemNextBookingList.get(0),
                "Несоответствие следующего бронирования вещи");
    }

    @Test
    void getItemLastBookingTest() {
        List<Booking> itemLastBookingList = bookingRepository.getItemLastBookings(
                PageRequest.of(0, 1), item1, LocalDateTime.now(), BookingStatus.APPROVED);
        Assertions.assertEquals(booking2, itemLastBookingList.get(0),
                "Несоответствие последнего бронирования вещи");
    }

    @Test
    void getBookerPastBookingsOfCertainItem() {
        List<Booking> itemLastBookingList = bookingRepository.getBookerPastBookingsOfCertainItem(
                PageRequest.of(0, 5), item1, user2, BookingStatus.APPROVED, LocalDateTime.now());
        Assertions.assertEquals(booking2, itemLastBookingList.get(0),
                "Несоответствие последнего бронирования вещи пользователем");
    }
}
