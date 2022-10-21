package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingRequestDto newBooking,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        validateBookingForAdding(newBooking);
        return bookingClient.addBooking(newBooking, userId);
    }

    private void validateBookingForAdding(BookingRequestDto newBooking) {
        if (!newBooking.getEnd().isAfter(newBooking.getStart())) {
            log.warn("Добавление бронирования с некорректными датами начала и окончания - {}", newBooking);
            throw new ValidationException("Ошибка добавления бронирования. " +
                    "Дата начала бронирования не может быть позже даты окончания");
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable int bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(value = "X-Sharer-User-Id") int ownerId) {
        return bookingClient.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PathVariable int bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookingList(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") int bookerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return bookingClient.getUserBookingList(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookingList(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") int ownerId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return bookingClient.getOwnerBookingList(state, ownerId, from, size);
    }
}
