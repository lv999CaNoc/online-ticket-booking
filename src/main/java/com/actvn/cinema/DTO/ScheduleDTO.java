package com.actvn.cinema.DTO;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Integer scheduleId;
    private String startTime;
    private String startDate;
    private Integer roomId;
}
