package ru.practicum.gateway.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BasicClient;
import ru.practicum.server.request.dto.ItemRequestDtoForRequest;

import java.util.Map;
import java.util.Optional;

@Component
public class ItemRequestClientImpl extends BasicClient implements ItemRequestClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClientImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getItemRequestWithResponse(int requestId, int userId) {
        return get(userId, "/" + requestId);
    }

    @Override
    public ResponseEntity<Object> getUserItemRequestList(int userId) {
        return get(userId);
    }

    @Override
    public ResponseEntity<Object> getPageableItemRequestList(int userId, int from, Optional<Integer> size) {
        Map<String, Object> params = Map.of(
                "from", from,
                "size", size.isPresent() ? size.get() : ""
        );
        return get(userId, "/all?from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> addItemRequest(ItemRequestDtoForRequest newItemRequest, int userId) {
        return post(userId, newItemRequest);
    }
}
