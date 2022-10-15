package ru.practicum.server.item.comment.repositoriy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.item.comment.model.Comment;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItem(Item item);
}
