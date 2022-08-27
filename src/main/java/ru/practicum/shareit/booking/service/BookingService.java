package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(BookingRequestDto newBooking, int bookerId);

    Booking approveBooking(int bookingId, Boolean approved, int ownerId);

    Booking getBooking(int bookingId, int userId);

    List<Booking> getUserBookingList(String state, int bookerId);

    List<Booking> getOwnerBookingList(String state, int bookerId);
}
