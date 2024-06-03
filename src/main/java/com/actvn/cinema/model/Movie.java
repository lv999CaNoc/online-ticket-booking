package com.actvn.cinema.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "movie")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Tên phim không được để trống.")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movie_categories", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @Column(length = 1000)
    @NotBlank(message = "Mô tả phim không được để trống.")
    private String description;

    private Integer duration;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date releaseDate;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;
    private String director;
    private String actors;
    private Rated rated;

    private double likePercentage;
    private double revenuePercentage;

    @Column(length = 1000, name = "trailer_url")
    private String trailerURL;
    @Column(length = 1000, name = "movie_image_url")
    private String movieImageURl;
    @Column(length = 1000, name = "banner_image_url")
    private String bannerImageURl;

    public String getFormattedReleaseDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(releaseDate);
    }

    public String getFormattedEndDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(endDate);
    }

    public boolean isShowing() {
        Date now = java.sql.Date.valueOf(LocalDate.now());
        return (releaseDate.before(now) && endDate.after(now));
    }
}