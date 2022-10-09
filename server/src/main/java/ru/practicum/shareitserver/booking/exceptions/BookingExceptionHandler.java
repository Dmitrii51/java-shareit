package ru.practicum.shareitserver.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler(UnsupportedStatusException.class)
    protected ResponseEntity<Map<String, String>> handleUnsupportedStatusException(
            UnsupportedStatusException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("error", exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
