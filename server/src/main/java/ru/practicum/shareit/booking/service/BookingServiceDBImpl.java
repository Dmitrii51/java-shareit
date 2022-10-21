package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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


@Slf4j
@Service
public class BookingServiceDBImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceDBImpl(BookingRepository bookingRepository,
                                UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public Booking addBooking(BookingRequestDto newBooking, int bookerId) {
        Item bookingItem = itemService.getItem(newBooking.getItemId());
        validateBookingForAdding(newBooking, bookerId, bookingItem);
        User booker = userService.getUser(bookerId);
        Booking savedBooking = bookingRepository.save(
                BookingMapper.fromBookingRequestDto(newBooking, booker, bookingItem));
        log.info("Добавление нового бронирования c id {}", savedBooking.getId());
        return savedBooking;
    }

    private void validateBookingForAdding(BookingRequestDto newBooking, int bookerId, Item bookingItem) {
        if (bookerId == bookingItem.getOwner().getId()) {
            log.warn("Запрос пользователем c id - {} своей вещи - {}", bookerId, bookingItem);
            throw new ResourceNotFoundException("Вещь не может быть забронирована своим хозяином");
        }
        if (!bookingItem.getAvailable()) {
            log.warn("Запрос недоступной для бронирования вещи - {}", newBooking);
            throw new ValidationException("Ошибка добавления бронирования. " +
                    "Вещь недоступна для бронирования");
        }
    }

    public Booking approveBooking(int bookingId, Boolean approved, int ownerId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        validateBookingExistence(booking, bookingId);
        Booking requestBooking = booking.get();
        validateBookingForApprove(requestBooking, ownerId);
        if (approved) {
            requestBooking.setStatus(BookingStatus.APPROVED);
        } else {
            requestBooking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(requestBooking);
    }

    private void validateBookingForApprove(Booking requestBooking, int ownerId) {
        if (!requestBooking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Попытка повторного изменеия статуса бронирования {} пользователем c id - {}",
                    requestBooking, ownerId);
            throw new ValidationException("Статус вещи уже был изменен");
        }
        if (requestBooking.getItem().getOwner().getId() != ownerId) {
            log.warn("Попытка изменеия статуса бронирования -  {} пользователем c id - {}, " +
                    "не являющимся владельцем вещи", requestBooking, ownerId);
            throw new ResourceNotFoundException("Отсуствуют права для изменения статуса бронирования");
        }
    }

    public Booking getBooking(int bookingId, int userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        validateBookingExistence(booking, bookingId);
        Booking requestBooking = booking.get();
        validateRequestBooking(requestBooking, userId);
        return booking.get();
    }

    private void validateRequestBooking(Booking requestBooking, int userId) {
        if (requestBooking.getItem().getOwner().getId() != userId && requestBooking.getBooker().getId() != userId) {
            log.warn("Попытка запроса информации о бронировании -  {} пользователем c id - {}, " +
                    "не являющимся владельцем вещи или запросившим бронирование", requestBooking, userId);
            throw new ResourceNotFoundException("Отсуствуют права для просмотра информации о бронировании");
        }
    }

    private void validateBookingExistence(Optional<Booking> booking, int bookingId) {
        if (booking.isEmpty()) {
            log.warn("Запрос данных о несуществующем бронировании с id - {}", bookingId);
            throw new ResourceNotFoundException(String.format("Бронирования с id = %s не существует", bookingId));
        }
    }

    public List<Booking> getUserBookingList(String state, int bookerId, int from, int size) {
        User booker = userService.getUser(bookerId);
        switch (state) {
            case "ALL":
                return bookingRepository.getAllBookerBookings(
                        PageRequest.of(from / size, size), booker);
            case "CURRENT":
                return bookingRepository.getBookerCurrentBookings(
                        PageRequest.of(from / size, size), booker, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.getBookerPastBookings(
                        PageRequest.of(from / size, size), booker, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.getBookerFutureBookings(
                        PageRequest.of(from / size, size), booker, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.getBookerBookingsWithCertainStatus(
                        PageRequest.of(from / size, size), booker, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getBookerBookingsWithCertainStatus(
                        PageRequest.of(from / size, size), booker, BookingStatus.REJECTED);
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    public List<Booking> getOwnerBookingList(String state, int ownerId, int from, int size) {
        User owner = userService.getUser(ownerId);
        switch (state) {
            case "ALL":
                return bookingRepository.getAllOwnerBookings(
                        PageRequest.of(from / size, size), owner.getId());
            case "CURRENT":
                return bookingRepository.getOwnerCurrentBookings(
                        PageRequest.of(from / size, size),
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.getOwnerPastBookingsExcludingCertainStatus(
                        PageRequest.of(from / size, size),
                        owner.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            case "FUTURE":
                return bookingRepository.getOwnerFutureBookingsExcludingCertainStatus(
                        PageRequest.of(from / size, size),
                        owner.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            case "WAITING":
                return bookingRepository.getOwnerBookingsWithCertainStatus(
                        PageRequest.of(from / size, size),
                        owner.getId(), BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getOwnerBookingsWithCertainStatus(
                        PageRequest.of(from / size, size), owner.getId(), BookingStatus.REJECTED);
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}
