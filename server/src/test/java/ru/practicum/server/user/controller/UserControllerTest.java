package ru.practicum.server.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1, "testUser1", "testUser1@email.ru");
        user2 = new User(2, "testUser2", "testUser2@email.ru");
    }

    @Test
    void getUserEndpointTest() throws Exception {
        when(userService.getUser(user1.getId()))
                .thenReturn(user1);
        mockMvc.perform(get("/users/{userId}", user1.getId()))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(user1)));
    }

    @Test
    void getUserListEndpointTest() throws Exception {
        when(userService.getUserList())
                .thenReturn(List.of(user1, user2));
        mockMvc.perform(get("/users"))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(List.of(user1, user2))));
    }

    @Test
    void createUserEndpointTest() throws Exception {
        when(userService.addUser(user1))
                .thenReturn(user1);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(user1)));
    }

    @Test
    void updateUserEndpointTest() throws Exception {
        when(userService.updateUser(user1, user1.getId()))
                .thenReturn(user1);
        mockMvc.perform(patch("/users/{userId}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpectAll(status().isOk(), content().json(mapper.writeValueAsString(user1)));
    }

    @Test
    void deleteUserEndpointTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user1.getId()))
                .andExpectAll(status().isOk());
    }
}
