package ru.practicum.server.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.user.model.User;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    private final JacksonTester<UserDto> jsonUserDto;

    private UserDto userDto;

    @Autowired
    UserDtoTest(JacksonTester<UserDto> jsonUserDto) {
        this.jsonUserDto = jsonUserDto;
    }

    @BeforeEach
    void beforeEach() {
        User user1 = new User(1, "testUser1", "testUser1@email.ru");
        userDto = UserMapper.toUserDto(user1);
    }

    @Test
    void userDtoJsonTest() throws IOException {
        JsonContent<UserDto> json = jsonUserDto.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId());
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(json).extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }
}