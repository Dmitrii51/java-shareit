package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.booking.model.BookingStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingForItemDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int bookerId;
    private BookingStatus status;
}
