package ru.practicum.shareit.requests;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    private int id;

    private String description;

    @OneToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @Column(name = "created_date")
    private LocalDateTime created;
}
