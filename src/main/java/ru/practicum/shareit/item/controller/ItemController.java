package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validators.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(
            @PathVariable int itemId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemService.getItemWithBooking(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getUserItemList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return itemService.getUserItemList(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemListWithRequestedSearchParameters(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return itemService.getItemListWithRequestedSearchParameters(text, from, size)
                .stream().map((ItemMapper::toItemDto)).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto createItem(
            @Validated(OnCreate.class) @RequestBody ItemPostRequestDto newItem,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ItemMapper.toItemDto(itemService.addItem(newItem, userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @Valid @RequestBody CommentRequestDto newComment,
            @PathVariable int itemId,
            @RequestHeader(value = "X-Sharer-User-Id") int authorId) {
        return CommentMapper.toCommentDto(itemService.addComment(newComment, itemId, authorId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable int itemId, @RequestBody Item item,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ItemMapper.toItemDto(itemService.updateItem(item, itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }
}
