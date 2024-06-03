package com.actvn.cinema.DTO;

import lombok.Data;

@Data
public class OrderDTO {
    private Integer billId;
    private double price;
    private String description;
}
