package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.item.dto.ItemPostRequestDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositoriy.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceDBImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemServiceDBImpl itemService;
    private Item item1;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        item1 = new Item(
                1, "testItem1", "Item1 for test", true, user1, null);
        user2 = new User(2, "testUser2", "testUser2@email.ru");
        user3 = new User(3, "testUser3", "testUser3@email.ru");
    }

    @Test
    void getItemWithBookingTest() {
        Booking booking1 = new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(3, LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6), item1, user3, BookingStatus.APPROVED);
        Comment comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5));
        List<CommentDto> comments = List.of(CommentMapper.toCommentDto(comment1));
        ItemWithBookingDto item1WithBookingDto = ItemMapper.toItemWithBookingDto(
                item1,
                BookingMapper.toBookingForItemDto(booking1),
                BookingMapper.toBookingForItemDto(booking2),
                comments);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getItemLastBookings(
                any(PageRequest.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getItemNextBookings(
                any(PageRequest.class), any(Item.class), any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(booking2));
        when(commentRepository.findByItem(item1)).thenReturn(List.of(comment1));
        assertThat(itemService.getItemWithBooking(item1.getId(), user1.getId())).isEqualTo(item1WithBookingDto);
    }

    @Test
    void getNonExistentItemWithBookingTest() {
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.getItemWithBooking(item1.getId(), user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addItemTest() {
        ItemRequest request1 = new ItemRequest(
                1, "Test request1 of Item1", user3, LocalDateTime.now().minusDays(2));
        item1.setRequest(request1);
        ItemPostRequestDto item1Dto = ItemMapper.toItemPostRequestDto(item1);
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(itemRequestService.getItemRequest(anyInt())).thenReturn(request1);
        when(itemRepository.save(any(Item.class))).thenReturn(item1);
        Item savedItem = itemService.addItem(item1Dto, user1.getId());
        assertThat(savedItem).isEqualTo(item1);
    }

    @Test
    void updateItemTest() {
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1)).thenReturn(item1);
        Item updatedItem = itemService.updateItem(item1, item1.getId(), user1.getId());
        assertThat(updatedItem).isEqualTo(item1);
    }

    @Test
    void updateNonExistentItemTest() {
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.updateItem(item1, item1.getId(), user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateItemWithNullValuesTest() {
        Item itemWithNullValues = new Item();
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(item1)).thenReturn(item1);
        Item updatedItem = itemService.updateItem(itemWithNullValues, item1.getId(), user1.getId());
        assertThat(updatedItem).isEqualTo(item1);
    }

    @Test
    void updateItemByNotOwnerTest() {
        when(userService.getUser(user2.getId())).thenReturn(user2);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        assertThatThrownBy(() -> itemService.updateItem(item1, item1.getId(), user2.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getUserItemListTest() {
        ItemWithBookingDto item1WithBookingDto = ItemMapper.toItemWithBookingDto(
                item1,
                null,
                null,
                Collections.emptyList());
        List<ItemWithBookingDto> userItemList = List.of(item1WithBookingDto);
        when(userService.getUser(user1.getId())).thenReturn(user1);
        when(itemRepository.findByOwner(PageRequest.of(0, 5), user1))
                .thenReturn(List.of(item1));
        assertThat(itemService.getUserItemList(user1.getId(), 0, 5)).isEqualTo(userItemList);
    }

    @Test
    void getItemListWithRequestedSearchParametersTest() {
        when(itemRepository.search(any(PageRequest.class), anyString())).thenReturn(List.of(item1));
        assertThat(itemService.getItemListWithRequestedSearchParameters("Test", 0, 5))
                .isEqualTo(List.of(item1));
    }

    @Test
    void addCommentTest() {
        Comment comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now());
        CommentRequestDto comment1RequestDto = CommentMapper.toCommentRequestDto(comment1);
        Booking booking1 = new Booking(2, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item1, user2, BookingStatus.APPROVED);
        when(userService.getUser(user2.getId())).thenReturn(user2);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getBookerPastBookingsOfCertainItem(
                any(PageRequest.class), any(Item.class), any(User.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(booking1));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);
        assertThat(itemService.addComment(comment1RequestDto, item1.getId(), user2.getId())).isEqualTo(comment1);
    }

    @Test
    void addCommentToNonExistentBookingTest() {
        Comment comment1 = new Comment(
                1, "testComment", item1, user2, LocalDateTime.now().plusDays(5));
        CommentRequestDto comment1RequestDto = CommentMapper.toCommentRequestDto(comment1);
        when(userService.getUser(user2.getId())).thenReturn(user2);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.getBookerPastBookingsOfCertainItem(
                any(PageRequest.class), any(Item.class), any(User.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> itemService.addComment(comment1RequestDto, item1.getId(), user2.getId()))
                .isInstanceOf(ValidationException.class);
    }
}
