package ru.practicum.gateway.booking.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareitserver.booking.dto.BookingRequestDto;

public interface BookingClient {

    ResponseEntity<Object> addBooking(BookingRequestDto newBooking, int userId);

    ResponseEntity<Object> approveBooking(int bookingId, Boolean approved, int ownerId);

    ResponseEntity<Object> getBooking(int bookingId, int userId);

    ResponseEntity<Object> getUserBookingList(String state, int bookerId, int from, int size);

    ResponseEntity<Object> getOwnerBookingList(String state, int ownerId, int from, int size);
}
