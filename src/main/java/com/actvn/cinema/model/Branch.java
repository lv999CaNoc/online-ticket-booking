package com.actvn.cinema.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Table(name = "branch")
@Entity
@NoArgsConstructor
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String name;
    private String diaChi;
    private String thanhPho;

    @Pattern(regexp = "\\d{10}", message = "Vui lòng nhập 10 chữ số.")
    private String phoneNumber;
}
