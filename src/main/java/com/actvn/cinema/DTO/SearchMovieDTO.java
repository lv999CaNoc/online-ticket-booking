package com.actvn.cinema.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchMovieDTO {
    private List<String> rates;
    private List<String> categories;
    private String status;
}
