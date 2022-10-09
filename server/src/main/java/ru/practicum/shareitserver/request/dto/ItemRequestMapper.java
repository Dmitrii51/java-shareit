package ru.practicum.shareitserver.request.dto;


import ru.practicum.shareitserver.item.dto.ItemDto;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest fromItemRequestDtoForRequest(
            ItemRequestDtoForRequest itemRequestDtoForRequest, User requestor) {
        return new ItemRequest(
                null,
                itemRequestDtoForRequest.getDescription(),
                requestor,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestWithUsersResponseDto toItemRequestWithUsersResponseDto(
            ItemRequest itemRequest, List<ItemDto> suggestedItemsList) {
        return new ItemRequestWithUsersResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                suggestedItemsList
        );
    }

    public static ItemRequestDtoForRequest toItemRequestDtoForRequest(ItemRequest itemRequest) {
        return new ItemRequestDtoForRequest(
                itemRequest.getDescription()
        );
    }
}
