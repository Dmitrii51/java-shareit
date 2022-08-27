package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStartIsAfterOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndEndIsBeforeOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            User booker, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findByBookerAndStatusOrderByStartDesc(
            User booker, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerOrderByStartDesc(int ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "AND b.status != ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStartIsAfterOrderByStartDesc(int ownerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status != ?3 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndEndIsBeforeOrderByStartDesc(int ownerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStartIsAfterAndEndIsBeforeOrderByStartDesc(
            int ownerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStatusLikeOrderByStartDesc(
            int ownerId, BookingStatus status);

    List<Booking> findByItemAndStartIsAfterAndStatusOrderByStart(
            PageRequest limit, Item item, LocalDateTime now, BookingStatus status);

    List<Booking> findByItemAndStartIsBeforeAndStatusOrderByStartDesc(
            PageRequest limit, Item item, LocalDateTime now, BookingStatus status);

    List<Booking> findByItemAndBookerAndStatusAndEndIsBefore(
            PageRequest limit, Item item, User booker, BookingStatus status, LocalDateTime now);
}
