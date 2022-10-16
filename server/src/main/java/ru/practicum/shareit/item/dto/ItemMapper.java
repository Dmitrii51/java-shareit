package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

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

    public static Item fromItemPatchRequestDto(ItemPatchRequestDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }
}
