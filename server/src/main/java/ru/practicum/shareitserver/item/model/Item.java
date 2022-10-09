package ru.practicum.shareitserver.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.validators.OnCreate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Название вещи не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Описание вещи не может состоять только из пробелов", groups = OnCreate.class)
    @NotEmpty(message = "Описание вещи не может быть пустым", groups = OnCreate.class)
    private String description;

    @Column(name = "is_available")
    @NotNull(message = "Статус доступности обязателен для заполнения", groups = OnCreate.class)
    private Boolean available;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
