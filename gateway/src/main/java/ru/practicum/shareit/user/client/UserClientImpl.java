package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BasicClient;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserClientImpl extends BasicClient implements UserClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClientImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getUser(int userId) {
        return get(userId, "/" + userId);
    }

    @Override
    public ResponseEntity<Object> getUserList() {
        return get();
    }

    @Override
    public ResponseEntity<Object> addUser(UserDto newUser) {
        return post(newUser);
    }

    @Override
    public ResponseEntity<Object> updateUser(UserDto user, int userId) {
        return patch(userId, "/" + userId, user);
    }

    @Override
    public ResponseEntity<Object> deleteUser(int userId) {
        return delete(userId, "/" + userId);
    }
}
