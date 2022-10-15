package ru.practicum.server.item.service;


import ru.practicum.server.item.comment.dto.CommentRequestDto;
import ru.practicum.server.item.comment.model.Comment;
import ru.practicum.server.item.dto.ItemPostRequestDto;
import ru.practicum.server.item.dto.ItemWithBookingDto;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemWithBookingDto getItemWithBooking(int itemId, int userId);

    Item getItem(int itemId);

    Item addItem(ItemPostRequestDto item, int userId);

    Item updateItem(Item item, int itemId, int userId);

    List<ItemWithBookingDto> getUserItemList(int userId, int from, int size);

    List<Item> getItemListWithRequestedSearchParameters(String text, int from, int size);

    Comment addComment(CommentRequestDto newComment, int itemId, int authorId);

    void deleteItem(int id);
}
