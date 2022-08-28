package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repositoriy.CommentRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositoriy.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceDBImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceDBImpl(UserService userService,
                             ItemRepository itemRepository,
                             BookingRepository bookingRepository,
                             CommentRepository commentRepository
    ) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemWithBookingDto getItemWithBooking(int id, int userId) {
        return getItemDtoWithBooking(getItem(id), userId);
    }

    @Override
    public Item getItem(int id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            log.warn("Запрос данных о несуществующей вещи с id - {}", id);
            throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", id));
        }
        return item.get();
    }

    @Override
    public Item addItem(Item newItem, int userId) {
        User owner = userService.getUser(userId);
        newItem.setOwner(owner);
        if (newItem.getId() == null) {
            Item savedItem = itemRepository.save(newItem);
            log.info("Добавление новой вещи c id {}", savedItem.getId());
            return savedItem;
        } else {
            log.warn("Добавление вещи с некорректным id - {}", newItem);
            throw new ValidationException("Ошибка добавления вещи. " +
                    "Id вещи должен быть равен null");
        }
    }

    @Override
    public Item updateItem(Item item, int itemId, int userId) {
        userService.getUser(userId);
        if (itemRepository.findById(itemId).isEmpty()) {
            log.warn("Попытка обновления несуществующей вещи - {}", item);
            throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", itemId));
        }
        Item savedItem = itemRepository.findById(itemId).get();
        if (savedItem.getOwner().getId() == userId) {
            item.setId(savedItem.getId());
            item.setOwner(savedItem.getOwner());
            if (item.getName() == null) {
                item.setName(savedItem.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(savedItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(savedItem.getAvailable());
            }
            itemRepository.save(item);
            log.info("Изменение информации о вещи с id {}", item.getId());
            return item;
        } else {
            log.warn("Попытка изменения информации о вещи пользователем, не являющимся хозяином");
            throw new ForbiddenException("Отсутствуют права для изменения информации о данной вещи");
        }
    }

    @Override
    public List<ItemWithBookingDto> getUserItemList(int userId) {
        User user = userService.getUser(userId);
        List<Item> userItemList = itemRepository.findByOwner(user);
        if (!userItemList.isEmpty()) {
            return userItemList.stream()
                    .map(item -> getItemDtoWithBooking(item, userId))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<Item> getItemListWithRequestedSearchParameters(String text) {
        List<Item> filteredBySearchParameterItemList = new ArrayList<>();
        if (!text.isBlank() && !text.isEmpty()) {
            filteredBySearchParameterItemList = itemRepository.search(text);
        }
        return filteredBySearchParameterItemList;
    }

    @Override
    public void deleteItem(int id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            log.warn("Попытка удаления несуществующей вещи с id - {}", id);
            throw new ResourceNotFoundException(String.format("Вещи с id = %s не существует", id));
        }
        itemRepository.deleteById(id);
        log.info("Удаление вещи с id {}", id);
    }

    private ItemWithBookingDto getItemDtoWithBooking(Item item, int userId) {
        BookingForItemDto lastBooking;
        BookingForItemDto nextBooking;
        List<Booking> itemPastBookingList = bookingRepository.getItemLastBookings(
                PageRequest.of(0, 1), item, LocalDateTime.now(), BookingStatus.APPROVED);
        if (itemPastBookingList.isEmpty() || item.getOwner().getId() != userId) {
            lastBooking = null;
        } else {
            lastBooking = BookingMapper.toBookingForItemDto(itemPastBookingList.get(0));
        }
        List<Booking> itemFutureBookingList = bookingRepository.getItemNextBookings(
                PageRequest.of(0, 1), item, LocalDateTime.now(), BookingStatus.APPROVED);
        if (itemFutureBookingList.isEmpty() || item.getOwner().getId() != userId) {
            nextBooking = null;
        } else {
            nextBooking = BookingMapper.toBookingForItemDto(itemFutureBookingList.get(0));
        }
        List<CommentDto> comments = commentRepository.findByItem(item)
                .stream()
                .map((CommentMapper::toCommentDto))
                .collect(Collectors.toList());
        return ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, comments);
    }

    public Comment addComment(CommentRequestDto newComment, int itemId, int authorId) {
        User author = userService.getUser(authorId);
        Item commentedItem = getItem(itemId);
        List<Booking> authorBooking = bookingRepository.getBookerPastBookingsOfCertainItem(
                PageRequest.of(0, 1), commentedItem, author, BookingStatus.APPROVED, LocalDateTime.now());
        if (authorBooking.isEmpty()) {
            log.warn("Попытка оставить комментарий к вещи с id - {} пользователем с id - {}, " +
                    "не бравшим данную вещь в аренду", itemId, authorId);
            throw new ValidationException(
                    String.format("Вещь с id = %s не была у пользлвателя с id = %s в аренде", itemId, authorId));
        }
        return commentRepository.save(
                CommentMapper.fromCommentRequestDto(newComment, commentedItem, author));
    }
}
