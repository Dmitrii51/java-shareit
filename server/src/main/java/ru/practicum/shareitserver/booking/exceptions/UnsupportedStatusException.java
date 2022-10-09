package ru.practicum.shareitserver.booking.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(final String message) {
        super(message);
    }
}
