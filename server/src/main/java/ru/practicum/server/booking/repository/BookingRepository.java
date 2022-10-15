package ru.practicum.server.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookerBookings(PageRequest limit, User booker);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerFutureBookings(PageRequest limit, User booker, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerPastBookings(PageRequest limit, User booker, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerCurrentBookings(
            PageRequest limit, User booker, LocalDateTime start, LocalDateTime end
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getBookerBookingsWithCertainStatus(
            PageRequest limit, User booker, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> getAllOwnerBookings(PageRequest limit, int ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "AND b.status != ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerFutureBookingsExcludingCertainStatus(
            PageRequest limit, int ownerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status != ?3 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerPastBookingsExcludingCertainStatus(
            PageRequest limit, int ownerId, LocalDateTime now, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?3 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerCurrentBookings(
            PageRequest limit, int ownerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getOwnerBookingsWithCertainStatus(
            PageRequest limit, int ownerId, BookingStatus status);

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
