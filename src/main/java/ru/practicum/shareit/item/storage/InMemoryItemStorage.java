package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Item> itemList;
    private int uniqueId;

    public InMemoryItemStorage() {
        itemList = new HashMap<>();
    }

    @Override
    public Item getItem(int id) {
        if (itemList.containsKey(id)) {
            return itemList.get(id);
        }
        log.warn("Запрос данных о несуществующей вещи с id - {}", id);
        throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", id));
    }

    @Override
    public List<Item> getItemList() {
        return new ArrayList<>(itemList.values());
    }

    @Override
    public Item addItem(Item newItem) {
        if (newItem.getId() == 0) {
            uniqueId += 1;
            newItem.setId(uniqueId);
            itemList.put(newItem.getId(), newItem);
            log.info("Добавление новой вещи c id {}", newItem.getId());
        } else {
            log.warn("Добавление вещи с некорректным id - {}", newItem);
            throw new ValidationException("Ошибка добавления вещи. " +
                    "Id вещи должен быть равен 0");
        }
        return newItem;
    }

    @Override
    public Item updateItem(Item item, int itemId) {
        if (itemList.containsKey(itemId)) {
            Item savedItem = itemList.get(itemId);
            item.setId(savedItem.getId());
            item.setOwner(savedItem.getOwner());
            if (item.getName() == null) {
                item.setName(savedItem.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(savedItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(savedItem.getAvailable());
            }
            itemList.put(itemId, item);
            log.info("Изменение информации о вещи с id {}", item.getId());
        } else {
            log.warn("Попытка обновления несуществующей вещи - {}", item);
            throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", itemId));
        }
        return item;
    }

    @Override
    public void deleteItem(int id) {
        if (itemList.containsKey(id)) {
            itemList.remove(id);
            log.info("Удаление вещи с id {}", id);
        } else {
            log.warn("Попытка удаления несуществующей вещи с id - {}", id);
            throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", id));
        }
    }

    @Override
    public void deleteItemList() {
        itemList.clear();
        log.info("Удаление списка вещей");
    }
}
