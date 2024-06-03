package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.MovieNotFoundException;
import com.actvn.cinema.model.Category;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Rated;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.BillRepository;
import com.actvn.cinema.repositories.CategoryRepository;
import com.actvn.cinema.repositories.MovieRepository;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private BillRepository billRepository;

    @Override
    public List<Movie> listAllMovie() {
        return movieRepository.findAll();
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public List<Movie> listMovieNowShowing() {
        Date now = java.sql.Date.valueOf(LocalDate.now());
        return movieRepository.findByReleaseDateBeforeAndEndDateAfter(now, now);
    }

    @Override
    public List<Movie> listMovieComingSoon() {
        return movieRepository.findByReleaseDateAfter(java.sql.Date.valueOf(LocalDate.now()));
    }

    @Override
    public Movie getMovieById(Integer id) throws MovieNotFoundException {
        Optional<Movie> result = movieRepository.findById(id);
        if (result.isPresent()) return result.get();
        throw new MovieNotFoundException("Không tìm thấy phim với id = " + id);
    }

    @Override
    public List<Movie> listMovieAdvantage(String search) throws MovieNotFoundException {
        List<Movie> movies = movieRepository.searchAdvantage(search);
        if (movies.isEmpty()) throw new MovieNotFoundException("Không tìm thấy phim với từ khóa " + search);
        return movies;
    }

    @Override
    public void deleteById(Integer id) throws MovieNotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            movieRepository.deleteById(id);
        } else {
            throw new MovieNotFoundException("Không tìm thấy phim có id = " + id);
        }
    }

    @Override
    public List<Movie> listTop3ByOrderByLikePercentageDesc() {
        return movieRepository.findTop3ByOrderByLikePercentageDesc();
    }

    @Override
    public List<Movie> filterMovie(SearchMovieDTO searchMovieDTO) throws MovieNotFoundException {
        List<Movie> movies = new ArrayList<>();
        List<Rated> rateOptions = new ArrayList<>();
        List<Integer> categoriesId = new ArrayList<>();
        if (searchMovieDTO.getRates()!=null && !searchMovieDTO.getRates().isEmpty()) {
            for (String rawRated : searchMovieDTO.getRates()) {
                Rated rated = Rated.valueOf(rawRated);
                rateOptions.add(rated);
            }
        } else {
            rateOptions = Arrays.asList(Rated.values());
        }
        if (searchMovieDTO.getCategories()!=null && !searchMovieDTO.getCategories().isEmpty()) {
            for (String rawCateId : searchMovieDTO.getCategories()) {
                Integer id = Integer.parseInt(rawCateId);
                categoriesId.add(id);
            }
        } else {
            for (Category category : categoryRepository.findAll()) {
                categoriesId.add(category.getId());
            }
        }
        Date now = new Date();
        if (searchMovieDTO.getStatus().equals("comming-soon")) {
            movies = movieRepository.filterMovieComingSoon(rateOptions, categoriesId, now);
        } else if (searchMovieDTO.getStatus().equals("showing")) {
            movies = movieRepository.filterMovieNowShowing(rateOptions, categoriesId, now);
        }else{
            movies = movieRepository.findTop3ByOrderByLikePercentageDesc();
        }
        if (movies.isEmpty()) throw new MovieNotFoundException("Không tìm thấy phim phù hợp");
        return movies;
    }

    public List<Movie> getUserWatchedMovies(Long userId) throws MovieNotFoundException{
        List<Ticket> ticketsByUserId = ticketRepository.findTicketsByUserId(userId);
        if (ticketsByUserId!=null && !ticketsByUserId.isEmpty()){
            List<Movie> distinctByTickets = movieRepository.findDistinctByTickets(ticketsByUserId);
            if (!distinctByTickets.isEmpty())
                return distinctByTickets;
        }
        throw new MovieNotFoundException("Không tìm thấy phim được xem bởi userId="+userId);
    }

    @Override
    public List<Movie> recommendMoviesForUser(Long userId) throws MovieNotFoundException {
        List<Movie> userWatchedMovies = getUserWatchedMovies(userId);
        List<Integer> moviesId = userWatchedMovies.stream().map(movie -> movie.getId()).collect(Collectors.toList());

        List<Integer> categoriesId = categoryRepository.getCategoryIdByMovieIdIn(moviesId);

        List<Movie> recommendedMovies = movieRepository.findByCategoryIdIn(categoriesId);
        recommendedMovies.removeIf(userWatchedMovies::contains);
        recommendedMovies = recommendedMovies.subList(0, Math.min(recommendedMovies.size(), 4));
        return recommendedMovies;
    }

}
