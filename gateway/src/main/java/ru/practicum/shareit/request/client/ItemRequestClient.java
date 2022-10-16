package ru.practicum.shareit.request.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequest;

import java.util.Optional;

public interface ItemRequestClient {

    ResponseEntity<Object> getItemRequestWithResponse(int requestId, int userId);

    ResponseEntity<Object> getUserItemRequestList(int userId);

    ResponseEntity<Object> getPageableItemRequestList(int userId, int from, Optional<Integer> size);

    ResponseEntity<Object> addItemRequest(ItemRequestDtoForRequest newItemRequest, int userId);
}
