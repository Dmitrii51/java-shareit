package ru.practicum.shareit.item.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchRequestDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;

public interface ItemClient {

    ResponseEntity<Object> getItemWithBooking(int itemId, int userId);

    ResponseEntity<Object> getUserItemList(int userId, int from, int size);

    ResponseEntity<Object> getItemListWithRequestedSearchParameters(String text, int from, int size);

    ResponseEntity<Object> addItem(ItemPostRequestDto newItem, int userId);

    ResponseEntity<Object> addComment(CommentRequestDto newComment, int itemId, int authorId);

    ResponseEntity<Object> updateItem(ItemPatchRequestDto item, int itemId, int userId);

    ResponseEntity<Object> deleteItem(int itemId);
}
