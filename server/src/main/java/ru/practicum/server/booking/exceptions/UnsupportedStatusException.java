package ru.practicum.server.booking.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(final String message) {
        super(message);
    }
}
