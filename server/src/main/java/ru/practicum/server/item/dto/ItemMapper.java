package ru.practicum.server.item.dto;


import ru.practicum.server.booking.dto.BookingForItemDto;
import ru.practicum.server.item.comment.dto.CommentDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (item.getRequest() == null) ? null : item.getRequest().getId()
        );
    }

    public static ItemWithBookingDto toItemWithBookingDto(
            Item item, BookingForItemDto lastBooking, BookingForItemDto nextBooking, List<CommentDto> comments) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

    public static Item fromItemPostRequestDto(
            ItemPostRequestDto itemPostRequestDto, User owner, ItemRequest itemRequest) {
        return new Item(
                null,
                itemPostRequestDto.getName(),
                itemPostRequestDto.getDescription(),
                itemPostRequestDto.getAvailable(),
                owner,
                itemRequest
        );
    }

    public static ItemPostRequestDto toItemPostRequestDto(Item item) {
        return new ItemPostRequestDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (item.getRequest() == null) ? null : item.getRequest().getId()
        );
    }
}
