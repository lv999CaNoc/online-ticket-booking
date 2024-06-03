package com.actvn.cinema.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ResponseSearchScheduleDTO {
    private Integer branchId;
    private String branchName;
    private List<ScheduleDTO> listSchedule;
}
