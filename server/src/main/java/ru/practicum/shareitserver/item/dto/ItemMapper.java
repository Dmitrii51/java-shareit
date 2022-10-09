package ru.practicum.shareitserver.item.dto;


import ru.practicum.shareitserver.booking.dto.BookingForItemDto;
import ru.practicum.shareitserver.item.comment.dto.CommentDto;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

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
