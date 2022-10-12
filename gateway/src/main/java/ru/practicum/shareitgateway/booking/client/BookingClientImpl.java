package ru.practicum.shareitgateway.booking.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BasicClient;
import ru.practicum.shareitserver.booking.dto.BookingRequestDto;

import java.util.Map;

@Component
public class BookingClientImpl extends BasicClient implements BookingClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClientImpl(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> addBooking(BookingRequestDto newBooking, int userId) {
        return post(userId, newBooking);
    }

    @Override
    public ResponseEntity<Object> approveBooking(int bookingId, Boolean approved, int ownerId) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch(ownerId, "/" + bookingId + "?approved={approved}", null,
                params);
    }

    @Override
    public ResponseEntity<Object> getBooking(int bookingId, int userId) {
        return get(userId, "/" + bookingId);
    }

    @Override
    public ResponseEntity<Object> getUserBookingList(String state, int bookerId, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(bookerId, "?state={state}&from={from}&size={size}", params);
    }

    @Override
    public ResponseEntity<Object> getOwnerBookingList(String state, int ownerId, int from, int size) {
        Map<String, Object> params = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(ownerId, "/owner?state={state}&from={from}&size={size}", params);
    }
}
