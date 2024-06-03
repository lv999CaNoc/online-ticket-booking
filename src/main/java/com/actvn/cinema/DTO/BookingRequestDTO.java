package com.actvn.cinema.DTO;

import lombok.Data;

import java.util.List;

@Data
public class BookingRequestDTO {
    private Long userId;
    private Integer scheduleId;
    private List<Long> listSeatIds;
}
