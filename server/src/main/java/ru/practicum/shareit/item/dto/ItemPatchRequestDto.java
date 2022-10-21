package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemPatchRequestDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
