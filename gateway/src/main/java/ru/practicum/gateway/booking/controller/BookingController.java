package ru.practicum.gateway.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.client.BookingClient;
import ru.practicum.server.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;


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
        return bookingClient.addBooking(newBooking, userId);
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
