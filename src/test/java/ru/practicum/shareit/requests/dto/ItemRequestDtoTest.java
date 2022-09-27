package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    private final JacksonTester<ItemRequestDto> jsonItemRequestDto;
    private final JacksonTester<ItemRequestDtoForRequest> jsonItemRequestDtoForRequest;
    private final JacksonTester<ItemRequestWithUsersResponseDto> jsonItemRequestWithUsersResponseDto;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoForRequest itemRequestDtoForRequest;
    private ItemRequestWithUsersResponseDto itemRequestWithUsersResponseDto;

    @Autowired
    ItemRequestDtoTest(JacksonTester<ItemRequestDto> jsonItemRequestDto,
                       JacksonTester<ItemRequestDtoForRequest> jsonItemRequestDtoForRequest,
                       JacksonTester<ItemRequestWithUsersResponseDto> jsonItemRequestWithUsersResponseDto) {
        this.jsonItemRequestDto = jsonItemRequestDto;
        this.jsonItemRequestDtoForRequest = jsonItemRequestDtoForRequest;
        this.jsonItemRequestWithUsersResponseDto = jsonItemRequestWithUsersResponseDto;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        User user2 = new User(2, "testUser2", "testUser2@email.ru");

        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user2, LocalDateTime.now());

        Item item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, request1);

        itemRequestDto = ItemRequestMapper.toItemRequestDto(request1);
        itemRequestDtoForRequest = ItemRequestMapper.toItemRequestDtoForRequest(request1);
        itemRequestWithUsersResponseDto = ItemRequestMapper.toItemRequestWithUsersResponseDto(
                request1, List.of(ItemMapper.toItemDto(item1)));
    }

    @Test
    void itemRequestDtoTest() throws IOException {
        JsonContent<ItemRequestDto> json = jsonItemRequestDto.write(itemRequestDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void itemRequestDtoForRequestTest() throws IOException {
        JsonContent<ItemRequestDtoForRequest> json = jsonItemRequestDtoForRequest.write(itemRequestDtoForRequest);

        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDtoForRequest.getDescription());
    }

    @Test
    void itemRequestWithUsersResponseDtoTest() throws IOException {
        JsonContent<ItemRequestWithUsersResponseDto> json =
                jsonItemRequestWithUsersResponseDto.write(itemRequestWithUsersResponseDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestWithUsersResponseDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestWithUsersResponseDto.getDescription());
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestWithUsersResponseDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(json).extractingJsonPathValue("$.items.size()")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().size());
        assertThat(json).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().get(0).getId());
        assertThat(json).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().get(0).getName());
        assertThat(json).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().get(0).getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().get(0).getAvailable());
        assertThat(json).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestWithUsersResponseDto.getItems().get(0).getRequestId());
    }
}
