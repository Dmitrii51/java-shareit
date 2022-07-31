package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ItemServiceImpl implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(UserStorage userStorage, ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public Item addItem(Item newItem, int userId) {
        User owner = userStorage.getUser(userId);
        newItem.setOwner(owner);
        return itemStorage.addItem(newItem);
    }

    @Override
    public Item updateItem(Item item, int itemId, int userId) {
        userStorage.getUser(userId);
        Item savedItem = itemStorage.getItem(itemId);
        if (savedItem.getOwner().getId() == userId) {
            return itemStorage.updateItem(item, itemId);
        }
        log.warn("Попытка изменения информации о вещи пользователем, не являющимся хозяином");
        throw new ForbiddenException("Отсутствуют права для изменения информации о данной вещи");
    }

    @Override
    public List<Item> getUserItemList(int userId) {
        userStorage.getUser(userId);
        List<Item> itemList = itemStorage.getItemList();
        List<Item> filteredByUserIdItemList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getOwner().getId() == userId) {
                filteredByUserIdItemList.add(item);
            }
        }
        return filteredByUserIdItemList;
    }

    public List<Item> getItemListWithRequestedSearchParameters(String text) {
        List<Item> itemList = itemStorage.getItemList();
        List<Item> filteredBySearchParameterItemList = new ArrayList<>();
        if (!text.isBlank() && !text.isEmpty()) {
            for (Item item : itemList) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true)) {
                    filteredBySearchParameterItemList.add(item);
                }
            }
        }
        return filteredBySearchParameterItemList;
    }
}
