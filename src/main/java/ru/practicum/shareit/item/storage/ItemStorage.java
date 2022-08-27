package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item getItem(int id);

    List<Item> getItemList();

    Item addItem(Item item);

    Item updateItem(Item item, int itemId);

    void deleteItem(int id);

    void deleteItemList();
}
