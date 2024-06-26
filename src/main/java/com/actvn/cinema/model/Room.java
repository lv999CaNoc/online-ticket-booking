package com.actvn.cinema.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Table(name="room")
@Entity
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên phòng không được để trống")
    private String name;

    @NotNull(message = "Sức chứa không được để trống")
    private Integer capacity;

    @ManyToOne
    @JoinColumn(nullable = false, name = "branch_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Branch branch;
}
