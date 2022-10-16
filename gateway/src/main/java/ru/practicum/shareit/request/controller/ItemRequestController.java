package ru.practicum.shareit.request.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoForRequest;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @PathVariable int requestId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemRequestClient.getItemRequestWithResponse(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemRequestClient.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPageableItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam Optional<Integer> size) {
        return itemRequestClient.getPageableItemRequestList(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody ItemRequestDtoForRequest newItemRequest,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemRequestClient.addItemRequest(newItemRequest, userId);
    }
}
