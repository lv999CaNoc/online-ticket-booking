package com.actvn.cinema.service;

import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> listAllMovie() throws NotFoundException;

    Movie save(Movie movie) throws IllegalArgumentException;

    List<Movie> listMovieNowShowing() throws NotFoundException;

    List<Movie> listMovieComingSoon() throws NotFoundException;

    Movie getMovieById(Integer id) throws NotFoundException;

    List<Movie> search(String search) throws NotFoundException;

    void deleteById(Integer id) throws NotFoundException;

    List<Movie> listTop3ByOrderByLikePercentageDesc() throws NotFoundException;

    List<Movie> filterMovie(SearchMovieDTO searchMovieDTO) throws NotFoundException;

    List<Movie> recommendMoviesForUser(Long userId) throws NotFoundException;
}
