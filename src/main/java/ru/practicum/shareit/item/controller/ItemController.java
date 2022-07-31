package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.validators.OnCreate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemStorage itemStorage;
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemStorage itemStorage, ItemService itemService) {
        this.itemStorage = itemStorage;
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getUserItemList(@RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return itemService.getUserItemList(userId)
                .stream().map((ItemMapper::toItemDto)).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getItemListWithRequestedSearchParameters(@RequestParam String text) {
        return itemService.getItemListWithRequestedSearchParameters(text)
                .stream().map((ItemMapper::toItemDto)).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto createItem(@Validated(OnCreate.class) @RequestBody Item newItem,
                              @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ItemMapper.toItemDto(itemService.addItem(newItem, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId, @RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        return ItemMapper.toItemDto(itemService.updateItem(item, itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteUser(@PathVariable int itemId) {
        itemStorage.deleteItem(itemId);
    }
}
