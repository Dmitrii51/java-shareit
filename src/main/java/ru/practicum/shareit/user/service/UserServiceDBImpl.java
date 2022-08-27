package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ResourceNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceDBImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceDBImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.warn("Запрос данных о несуществующем пользователе с id - {}", id);
            throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", id));
        }
        return user.get();
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User newUser) {
        validateNewUser(newUser);
        User savedUser = userRepository.save(newUser);
        log.info("Добавление нового пользователя c id {}", newUser.getId());
        return savedUser;
    }

    @Override
    public User updateUser(User user, int userId) {
        validateUpdatedUser(user, userId);
        User savedUser = userRepository.findById(userId).get();
        user.setId(savedUser.getId());
        if (user.getEmail() == null) {
            user.setEmail(savedUser.getEmail());
        }
        if (user.getName() == null) {
            user.setName(savedUser.getName());
        }
        userRepository.save(user);
        log.info("Изменение информации о пользователе с id {}", user.getId());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            log.warn("Попытка удаления несуществующего пользователя с id - {}", id);
            throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", id));
        }
        userRepository.deleteById(id);
        log.info("Удаление пользователя с id {}", id);
    }

    private void validateNewUser(User newUser) {
        if (newUser.getId() != null) {
            log.warn("Добавление пользователя с некорректным id - {}", newUser);
            throw new ValidationException("Ошибка добавления пользователя. " +
                    "Id пользователя должен быть null");
        }
    }

    private void validateUpdatedUser(User user, int userId) {
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("Попытка обновления несуществующего пользователя - {}", user);
            throw new ResourceNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
    }
}
