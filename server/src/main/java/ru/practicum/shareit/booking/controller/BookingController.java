package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(
            @RequestBody BookingRequestDto newBooking,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(newBooking, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable int bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(value = "X-Sharer-User-Id") int ownerId) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, approved, ownerId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable int bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getUserBookingList(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") int bookerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "5") int size) {
        return bookingService.getUserBookingList(state, bookerId, from, size)
                .stream().map((BookingMapper::toBookingDto))
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookingList(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(value = "X-Sharer-User-Id") int ownerId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "5") int size) {
        return bookingService.getOwnerBookingList(state, ownerId, from, size)
                .stream().map((BookingMapper::toBookingDto))
                .collect(Collectors.toList());
    }
}
