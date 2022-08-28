package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (newBooking.getEnd().isAfter(newBooking.getStart())) {
            Item bookingItem = itemService.getItem(newBooking.getItemId());
            if (bookerId == bookingItem.getOwner().getId()) {
                log.warn("Запрос пользователем c id - {} своей вещи - {}", bookerId, bookingItem);
                throw new ResourceNotFoundException("Вещь не может быть забронирована своим хозяином");
            }
            User booker = userService.getUser(bookerId);
            if (!bookingItem.getAvailable()) {
                log.warn("Запрос недоступной для бронирования вещи - {}", newBooking);
                throw new ValidationException("Ошибка добавления бронирования. " +
                        "Вещь недоступна для бронирования");
            }
            Booking savedBooking = bookingRepository.save(
                    BookingMapper.fromBookingRequestDto(newBooking, booker, bookingItem));
            log.info("Добавление нового бронирования c id {}", savedBooking.getId());
            return savedBooking;
        } else {
            log.warn("Добавление бронирования с некорректными датами начала и окончания - {}", newBooking);
            throw new ValidationException("Ошибка добавления бронирования. " +
                    "Дата начала бронирования не может быть позже даты окончания");
        }
    }

    public Booking approveBooking(int bookingId, Boolean approved, int ownerId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.warn("Запрос данных о несуществующем бронировании с id - {}", bookingId);
            throw new ResourceNotFoundException(String.format("Бронирования с id = %s не существует", bookingId));
        }
        Booking requestBooking = booking.get();
        if (requestBooking.getItem().getOwner().getId() == ownerId &&
                requestBooking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                requestBooking.setStatus(BookingStatus.APPROVED);

            } else {
                requestBooking.setStatus(BookingStatus.REJECTED);
            }
            return bookingRepository.save(requestBooking);
        } else if (requestBooking.getItem().getOwner().getId() != ownerId) {
            log.warn("Попытка изменеия статуса бронирования -  {} пользователем c id - {}, " +
                    "не являющимся владельцем вещи", requestBooking, ownerId);
            throw new ResourceNotFoundException("Отсуствуют права для изменения статуса бронирования");
        }
        log.warn("Попытка повторного изменеия статуса бронирования {} пользователем c id - {}",
                requestBooking, ownerId);
        throw new ValidationException("Статус вещи уже был изменен");
    }

    public Booking getBooking(int bookingId, int userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.warn("Запрос данных о несуществующем бронировании с id - {}", bookingId);
            throw new ResourceNotFoundException(String.format("Бронирования с id = %s не существует", bookingId));
        }
        Booking requestBooking = booking.get();
        if (requestBooking.getItem().getOwner().getId() == userId || requestBooking.getBooker().getId() == userId) {
            return requestBooking;
        }
        log.warn("Попытка запроса информации о бронировании -  {} пользователем c id - {}, " +
                "не являющимся владельцем вещи или запросившим бронирование", requestBooking, userId);
        throw new ResourceNotFoundException("Отсуствуют права для изменения статуса бронирования");
    }

    public List<Booking> getUserBookingList(String state, int bookerId) {
        User booker = userService.getUser(bookerId);
        switch (state) {
            case "ALL":
                return bookingRepository.getAllBookerBookings(booker);
            case "CURRENT":
                return bookingRepository.getBookerCurrentBookings(
                        booker, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.getBookerPastBookings(
                        booker, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.getBookerFutureBookings(
                        booker, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.getBookerBookingsWithCertainStatus(
                        booker, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getBookerBookingsWithCertainStatus(
                        booker, BookingStatus.REJECTED);
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    public List<Booking> getOwnerBookingList(String state, int ownerId) {
        User owner = userService.getUser(ownerId);
        switch (state) {
            case "ALL":
                return bookingRepository.getAllOwnerBookings(owner.getId());
            case "CURRENT":
                return bookingRepository.getOwnerCurrentBookings(
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.getOwnerPastBookingsExcludingCertainStatus(
                        owner.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            case "FUTURE":
                return bookingRepository.getOwnerFutureBookingsExcludingCertainStatus(
                        owner.getId(), LocalDateTime.now(), BookingStatus.REJECTED);
            case "WAITING":
                return bookingRepository.getOwnerBookingsWithCertainStatus(
                        owner.getId(), BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getOwnerBookingsWithCertainStatus(
                        owner.getId(), BookingStatus.REJECTED);
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}

