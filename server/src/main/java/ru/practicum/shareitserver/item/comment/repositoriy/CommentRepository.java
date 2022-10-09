package ru.practicum.shareitserver.item.comment.repositoriy;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.item.comment.model.Comment;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItem(Item item);
}
