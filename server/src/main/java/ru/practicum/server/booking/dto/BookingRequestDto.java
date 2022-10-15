package ru.practicum.server.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingRequestDto {

    @NotNull(message = "Дата начала бронирования обязательна для заполнения")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна для заполнения")
    @Future(message = "Дата окончания бронирования не может быть в пролом или настоящем")
    private LocalDateTime end;

    @NotNull(message = "Id вещи обязателен для заполнения")
    @Positive(message = "Id вещи должен быть положительным")
    private int itemId;
}
