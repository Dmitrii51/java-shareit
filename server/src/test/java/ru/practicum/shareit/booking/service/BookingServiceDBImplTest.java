package ru.practicum.shareit.booking.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceDBImplTest {

    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceDBImpl bookingService;
    @Mock
    private ItemService itemService;
    private Booking booking1;
    private Item item1;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, null);
        user2 = new User(2, "testUser2", "testUser2@email.ru");
        user3 = new User(3, "testUser3", "testUser3@email.ru");
        booking1 = new Booking(2, LocalDateTime.now(),
                LocalDateTime.now().plusDays(2), item1, user2, BookingStatus.WAITING);
    }

    @Test
    void addBookingTest() {
        when(itemService.getItem(any(Integer.class))).thenReturn(item1);
        when(userService.getUser(user2.getId())).thenReturn(user2);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        Assertions.assertThat(bookingService.addBooking(BookingMapper.toBookingRequestDto(booking1), user2.getId()))
                .isEqualTo(booking1);
    }

    @Test
    void addBookingWithWrongStartTest() {
        booking1.setStart(LocalDateTime.now().plusDays(3));
        assertThatThrownBy(() -> bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking1), user2.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void addBookingNotAvailableItemTest() {
        item1.setAvailable(false);
        when(itemService.getItem(any(Integer.class))).thenReturn(item1);
        assertThatThrownBy(() -> bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking1), user3.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void addBookingByItemOwner() {
        item1.setAvailable(false);
        when(itemService.getItem(any(Integer.class))).thenReturn(item1);
        assertThatThrownBy(() -> bookingService.addBooking(
                BookingMapper.toBookingRequestDto(booking1), user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer((approvedBooking) -> {
                    booking1.setStatus(BookingStatus.APPROVED);
                    return booking1;
                });
        Assertions.assertThat(bookingService.approveBooking(booking1.getId(), true, user1.getId()).getStatus())
                .isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void rejectBookingTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer((approvedBooking) -> {
                    booking1.setStatus(BookingStatus.REJECTED);
                    return booking1;
                });
        Assertions.assertThat(bookingService.approveBooking(booking1.getId(), false, user1.getId()).getStatus())
                .isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveNonExistentBookingTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.approveBooking(booking1.getId(), true, user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void approveBookingWithWrongStatusTest() {
        Booking approvedBooking = new Booking(2, booking1.getStart(),
                booking1.getEnd(), item1, user2, BookingStatus.APPROVED);
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.of(approvedBooking));
        assertThatThrownBy(() -> bookingService.approveBooking(booking1.getId(), true, user1.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.of(booking1));
        Assertions.assertThat(bookingService.getBooking(booking1.getId(), user1.getId()))
                .isEqualTo(booking1);
    }

    @Test
    void getNonExistentBookingTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingService.getBooking(booking1.getId(), user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getBookingNotAvailableForUserTest() {
        when(bookingRepository.findById(any(Integer.class))).thenReturn(Optional.of(booking1));
        assertThatThrownBy(() -> bookingService.getBooking(booking1.getId(), user3.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllUserBookingListTest() {
        when(userService.getUser(user2.getId())).thenReturn(user2);
        when(bookingRepository.getAllBookerBookings(any(PageRequest.class), any(User.class)))
                .thenReturn(List.of(booking1));
        Assertions.assertThat(bookingService.getUserBookingList("ALL", user2.getId(), 0, 5))
                .isEqualTo(List.of(booking1));
    }

    @Test
    void getWrongStatusUserBookingListTest() {
        assertThatThrownBy(() -> bookingService.getUserBookingList("GOOD", user2.getId(), 0, 5))
                .isInstanceOf(UnsupportedStatusException.class);
    }

    @Test
    void getAllOwnerBookingListTest() {
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(bookingRepository.getAllOwnerBookings(any(PageRequest.class), any(Integer.class)))
                .thenReturn(List.of(booking1));
        Assertions.assertThat(bookingService.getOwnerBookingList("ALL", user1.getId(), 0, 5))
                .isEqualTo(List.of(booking1));
    }

    @Test
    void getWrongStatusOwnerBookingListTest() {
        assertThatThrownBy(() -> bookingService.getOwnerBookingList("GOOD", user2.getId(), 0, 5))
                .isInstanceOf(UnsupportedStatusException.class);
    }
}
