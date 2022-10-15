package ru.practicum.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.server.booking.dto.BookingForItemDto;
import ru.practicum.server.item.comment.dto.CommentDto;

import java.util.List;


@AllArgsConstructor
@Data
public class ItemWithBookingDto implements Comparable<ItemWithBookingDto> {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;

    @Override
    public int compareTo(ItemWithBookingDto otherItem) {
        if (lastBooking == null && otherItem.getNextBooking() == null) {
            return 0;
        } else if (lastBooking == null && otherItem.getNextBooking() != null) {
            return 1;
        } else if (lastBooking != null && otherItem.getNextBooking() == null) {
            return -1;
        }
        return lastBooking.getStart().compareTo(otherItem.getNextBooking().getStart());
    }
}
