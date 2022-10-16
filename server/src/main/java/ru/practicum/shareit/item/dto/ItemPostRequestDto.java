package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemPostRequestDto {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
