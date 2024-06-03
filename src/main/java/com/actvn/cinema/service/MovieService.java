package com.actvn.cinema.service;

import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.MovieNotFoundException;
import com.actvn.cinema.model.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> listAllMovie();

    Movie save(Movie movie);

    List<Movie> listMovieNowShowing();

    List<Movie> listMovieComingSoon();

    Movie getMovieById(Integer id) throws MovieNotFoundException;

    List<Movie> listMovieAdvantage(String search) throws MovieNotFoundException;

    void deleteById(Integer id) throws MovieNotFoundException;

    List<Movie> listTop3ByOrderByLikePercentageDesc();

    List<Movie> filterMovie(SearchMovieDTO searchMovieDTO) throws MovieNotFoundException;

    List<Movie> recommendMoviesForUser(Long userId) throws MovieNotFoundException;
}
