package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query("SELECT i FROM ItemRequest i " +
            "WHERE i.requestor = ?1 " +
            "ORDER BY i.created DESC")
    List<ItemRequest> getUserItemRequestList(User user);

    @Query("SELECT i FROM ItemRequest i " +
            "WHERE i.requestor != ?1 " +
            "ORDER BY i.created DESC")
    List<ItemRequest> getPageableItemRequestList(PageRequest limit, User user);
}
