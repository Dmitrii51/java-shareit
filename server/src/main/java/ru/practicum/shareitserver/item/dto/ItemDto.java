package ru.practicum.shareitserver.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemDto {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
