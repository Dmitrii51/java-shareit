package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING("Бронирование ожидает одобрения"),
    APPROVED("Бронирование подтверждено владельцем"),
    REJECTED("Бронирование отклонено владельцем"),
    CANCELED("Бронирование отменено создателем");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public static String getDescription(BookingStatus status) {
        return status.description;
    }
}
