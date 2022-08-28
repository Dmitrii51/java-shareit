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

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookerBookings(User booker);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerFutureBookings(User booker, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerPastBookings(User booker, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerCurrentBookings(
            User booker, LocalDateTime start, LocalDateTime end
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerBookingsWithCertainStatus(
            User booker, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> getAllOwnerBookings(int ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "AND b.status != ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerFutureBookingsExcludingCertainStatus(int ownerId,
                                                               LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status != ?3 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerPastBookingsExcludingCertainStatus(int ownerId,
                                                             LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerCurrentBookings(
            int ownerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerBookingsWithCertainStatus(
            int ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getItemNextBookings(
            PageRequest limit, Item item, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item = ?1 " +
            "AND b.start < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getItemLastBookings(
            PageRequest limit, Item item, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item = ?1 " +
            "AND b.booker = ?2 " +
            "AND b.status = ?3 " +
            "AND b.end < ?4 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerPastBookingsOfCertainItem(
            PageRequest limit, Item item, User booker, BookingStatus status, LocalDateTime now);
}
