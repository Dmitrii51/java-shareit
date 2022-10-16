package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BasicClient;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemPatchRequestDto;
import ru.practicum.shareit.item.dto.ItemPostRequestDto;

import java.util.Map;

@Component
public class ItemClientImpl extends BasicClient implements ItemClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClientImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getItemWithBooking(int itemId, int userId) {
        return get(userId, "/" + itemId);
    }

    @Override
    public ResponseEntity<Object> getUserItemList(int userId, int from, int size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size
        );
        return get(userId, "?from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> getItemListWithRequestedSearchParameters(String text, int from, int size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size,
                "text", text
        );
        return get(null, "/search?text={text}&from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> addItem(ItemPostRequestDto newItem, int userId) {
        return post(userId, newItem);
    }

    @Override
    public ResponseEntity<Object> addComment(CommentRequestDto newComment, int itemId, int authorId) {
        return post(authorId, "/" + itemId + "/comment", newComment);
    }

    @Override
    public ResponseEntity<Object> updateItem(ItemPatchRequestDto item, int itemId, int userId) {
        return patch(userId, "/" + itemId, item);
    }

    @Override
    public ResponseEntity<Object> deleteItem(int itemId) {
        return delete(itemId, "/" + itemId);
    }
}
