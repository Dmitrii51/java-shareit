package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validators.OnCreate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Имя пользователя не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Имя пользователя не может быть пустым", groups = OnCreate.class)
    private String name;

    @Column(unique = true)
    @NotNull(message = "Электронная почта не может быть пустой", groups = OnCreate.class)
    @Email(message = "Электронная почта должна содержать символ @", groups = OnCreate.class)
    private String email;
}
