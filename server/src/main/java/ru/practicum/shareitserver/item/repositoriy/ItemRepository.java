package ru.practicum.shareitserver.item.repositoriy;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwner(PageRequest limit, User user);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = TRUE " +
            "AND (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%')))")
    List<Item> search(PageRequest limit, String text);

    @Query("SELECT i FROM Item i " +
            "WHERE i.request = ?1")
    List<Item> getItemsFotItemRequest(
            ItemRequest itemRequest);
}
