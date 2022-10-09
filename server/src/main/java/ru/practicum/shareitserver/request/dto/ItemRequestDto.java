package ru.practicum.shareitserver.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
}
