package com.actvn.cinema.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BookingRequestDTO {
    @NotBlank(message = "User ID không được để trống")
    private Long userId;
    @NotBlank(message = "Schedule ID không được để trống")
    private Integer scheduleId;
    @NotEmpty(message = "Danh sách ID ghế không được để trống")
    private List<Long> listSeatIds;
}
