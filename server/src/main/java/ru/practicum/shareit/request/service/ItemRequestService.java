package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestWithUsersResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {

    ItemRequest addItemRequest(ItemRequestDtoForRequest newItemRequest, int userId);

    List<ItemRequestWithUsersResponseDto> getUserItemRequestList(int userId);

    List<ItemRequestWithUsersResponseDto> getPageableItemRequestList(
            int userId, int from, Optional<Integer> size);

    ItemRequest getItemRequest(int requestId);

    ItemRequestWithUsersResponseDto getItemRequestWithResponse(int requestId, int userId);
}
