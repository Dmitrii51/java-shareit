package ru.practicum.shareitserver.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
