package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {

    private final UserService userService;
    private final EntityManager entityManager;

    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");
    }

    @Test
    @Transactional
    void getUserTest() {
        userService.addUser(user1);
        User savedUser = userService.getUser(user1.getId());
        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    @Transactional
    void getUserListTest() {
        userService.addUser(user1);
        userService.addUser(user2);
        Assertions.assertThat(userService.getUserList()).isEqualTo(List.of(user1, user2));
    }

    @Test
    @Transactional
    void addUserTest() {
        userService.addUser(user1);
        User savedUser = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.id = :id", User.class)
                .setParameter("id", user1.getId()).getSingleResult();
        Assertions.assertThat(userService.addUser(user1)).isEqualTo(user1);
    }

    @Test
    @Transactional
    void updateUserTest() {
        userService.addUser(user1);
        user1.setName("user1");
        userService.updateUser(user1, user1.getId());
        User savedUser = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.id = :id", User.class)
                .setParameter("id", user1.getId()).getSingleResult();
        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    @Transactional
    void deleteUserTest() {
        userService.addUser(user1);
        userService.deleteUser(user1.getId());
        TypedQuery<User> query = entityManager.createQuery(
                        "SELECT u FROM User u " +
                                "WHERE u.id = :id", User.class)
                .setParameter("id", user1.getId());
        assertThat(query.getResultList().size()).isEqualTo(0);
    }
}
