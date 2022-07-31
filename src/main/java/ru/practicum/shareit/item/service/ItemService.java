package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, int userId);

    Item updateItem(Item item, int itemId, int userId);

    List<Item> getUserItemList(int userId);

    List<Item> getItemListWithRequestedSearchParameters(String text);
}
