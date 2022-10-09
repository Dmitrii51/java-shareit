package ru.practicum.shareitserver.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareitserver.exceptions.ResourceNotFoundException;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceDBImplTest {

    @InjectMocks
    private UserServiceDBImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");
    }


    @Test
    void getUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        assertThat(userService.getUser(user1.getId())).isEqualTo(user1);
    }

    @Test
    void getNonExistentUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUser(user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserListTest() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        assertThat(userService.getUserList()).isEqualTo(List.of(user1, user2));
    }

    @Test
    void addUserTest() {
        when(userRepository.save(user1)).thenReturn(user1);
        assertThat(userService.addUser(user1)).isEqualTo(user1);
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);
        assertThat(userService.updateUser(user1, user1.getId())).isEqualTo(user1);
    }

    @Test
    void updateNonExistentUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(user1, user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void deleteUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        userService.deleteUser(user1.getId());
        verify(userRepository, times(1)).deleteById(user1.getId());
        verify(userRepository, times(1)).findById(user1.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteNonExistentUserTest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(user1, user1.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
