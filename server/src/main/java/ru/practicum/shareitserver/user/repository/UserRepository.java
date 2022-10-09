package ru.practicum.shareitserver.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findFirstByEmailIgnoreCase(String email);
}
