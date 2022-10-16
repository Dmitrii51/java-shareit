package ru.practicum.shareit.booking.dto;


import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static BookingRequestDto toBookingRequestDto(Booking booking) {
        return new BookingRequestDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId()
        );
    }

    public static Booking fromBookingRequestDto(BookingRequestDto bookingRequestDto,
                                                User booker, Item bookingItem) {
        return new Booking(
                null,
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                bookingItem, booker,
                BookingStatus.WAITING
        );
    }
}
