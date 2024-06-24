package com.actvn.cinema.DTO;

import com.actvn.cinema.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSearchMovieDTO {
    private Integer id;
    private String title;
    private Set<Category> categories;
    private String description;
    private Integer duration;
    private String formattedReleaseDate;
    private String ratedDescription;
    private double likePercentage;
    private double revenuePercentage;
    private String trailerURL;
    private String movieImageURl;
}
