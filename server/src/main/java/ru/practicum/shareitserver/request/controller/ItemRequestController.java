package ru.practicum.shareitserver.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestDtoForRequest;
import ru.practicum.shareitserver.request.dto.ItemRequestMapper;
import ru.practicum.shareitserver.request.dto.ItemRequestWithUsersResponseDto;
import ru.practicum.shareitserver.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithUsersResponseDto getItemRequest(@PathVariable int requestId,
                                                          @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemRequestService.getItemRequestWithResponse(requestId, userId);
    }


    @GetMapping
    public List<ItemRequestWithUsersResponseDto> getUserItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemRequestService.getUserItemRequestList(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithUsersResponseDto> getPageableItemRequestList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam Optional<Integer> size) {
        return itemRequestService.getPageableItemRequestList(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(
            @Valid @RequestBody ItemRequestDtoForRequest newItemRequest,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(newItemRequest, userId));
    }
}
