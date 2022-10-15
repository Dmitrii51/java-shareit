package ru.practicum.gateway.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.client.ItemClient;
import ru.practicum.server.item.comment.dto.CommentRequestDto;
import ru.practicum.server.item.dto.ItemPostRequestDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.validators.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @PathVariable int itemId,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemClient.getItemWithBooking(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemList(
            @RequestHeader(value = "X-Sharer-User-Id") int userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return itemClient.getUserItemList(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemListWithRequestedSearchParameters(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "5") @Min(1) int size) {
        return itemClient.getItemListWithRequestedSearchParameters(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @Validated(OnCreate.class) @RequestBody ItemPostRequestDto newItem,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemClient.addItem(newItem, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @Valid @RequestBody CommentRequestDto newComment,
            @PathVariable int itemId,
            @RequestHeader(value = "X-Sharer-User-Id") int authorId) {
        return itemClient.addComment(newComment, itemId, authorId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @PathVariable int itemId, @RequestBody Item item,
            @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemClient.updateItem(item, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable int itemId) {
        return itemClient.deleteItem(itemId);
    }
}
