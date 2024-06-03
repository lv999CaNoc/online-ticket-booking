package com.actvn.cinema.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchScheduleDTO {
    private Integer movieId;
    private String city;
    private Integer branchId;
    private String date;
}
