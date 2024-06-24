package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Category;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Rated;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.CategoryRepository;
import com.actvn.cinema.repositories.MovieRepository;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private Validator validator;

    @Override
    public List<Movie> listAllMovie() throws NotFoundException {
        List<Movie> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            log.warn("[LOG] Không có phim");
            throw new NotFoundException("Không có phim");
        }
        return movies;
    }

    @Override
    public Movie save(@Valid Movie movie) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(movie, "movie");
        validator.validate(movie, errors);
        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(message);
        }
        Movie movieSaved = movieRepository.save(movie);
        log.info("[LOG] Lưu phim {} thành công.", movieSaved.getTitle());
        return movieSaved;
    }

    @Override
    public List<Movie> listMovieNowShowing() throws NotFoundException {
        Date now = java.sql.Date.valueOf(LocalDate.now());
        List<Movie> movies = movieRepository.findByReleaseDateBeforeAndEndDateAfter(now, now);
        if (movies.isEmpty()){
            log.warn("[LOG] Không có phim đang được chiếu");
            throw new NotFoundException("Không có phim đang được chiếu");
        }
        return movies;
    }

    @Override
    public List<Movie> listMovieComingSoon() throws NotFoundException {
        List<Movie> movies = movieRepository.findByReleaseDateAfter(java.sql.Date.valueOf(LocalDate.now()));
        if (movies.isEmpty()){
            log.warn("[LOG] Không có phim sắp chiếu");
            throw new NotFoundException("Không có phim sắp chiếu");
        }
        return movies;
    }

    @Override
    public Movie getMovieById(Integer id) throws NotFoundException {
        Optional<Movie> result = movieRepository.findById(id);
        if (result.isPresent())
            return result.get();

        log.warn("[LOG] Không tìm thấy phim với id: {}", id);
        throw new NotFoundException("Không tìm thấy phim với id: " + id);
    }

    @Override
    public List<Movie> search(String search) throws NotFoundException {
        List<Movie> movies = movieRepository.search(search);
        if (movies.isEmpty()) {
            log.warn("[LOG] Không tìm thấy phim với từ khóa: {}", search);
            throw new NotFoundException("Không tìm thấy phim với từ khóa " + search);
        }
        return movies;
    }

    @Override
    public void deleteById(Integer id) throws NotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()) {
            log.info("[LOG] Xoá thành công phim có ID: ", id);
            movieRepository.deleteById(id);
        } else {
            log.warn("[LOG] Không tìm thấy phim có ID: {}", id);
            throw new NotFoundException("Không tìm thấy phim có ID:" + id);
        }
    }

    @Override
    public List<Movie> listTop3ByOrderByLikePercentageDesc() throws NotFoundException {
        List<Movie> top3ByOrderByLikePercentageDesc = movieRepository.findTop3ByOrderByLikePercentageDesc();
        if (top3ByOrderByLikePercentageDesc.isEmpty()){
            throw new NotFoundException("Không có phim");
        }
        return top3ByOrderByLikePercentageDesc;
    }

    @Override
    public List<Movie> filterMovie(SearchMovieDTO searchMovieDTO) throws NotFoundException {
        List<Movie> movies = new ArrayList<>();
        List<Rated> rateOptions = new ArrayList<>();
        List<Integer> categoriesId = new ArrayList<>();

        if (searchMovieDTO.getRates() != null && !searchMovieDTO.getRates().isEmpty()) {
            for (String rawRated : searchMovieDTO.getRates()) {
                Rated rated = Rated.valueOf(rawRated);
                rateOptions.add(rated);
            }
        } else {
            rateOptions = Arrays.asList(Rated.values());
        }
        if (searchMovieDTO.getCategories() != null && !searchMovieDTO.getCategories().isEmpty()) {
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
        } else {
            movies = movieRepository.findTop3ByOrderByLikePercentageDesc();
        }
        if (movies.isEmpty()) {
            log.warn("[LOG] Không tìm thấy phim phù hợp");
            throw new NotFoundException("Không tìm thấy phim phù hợp");
        }
        return movies;
    }

    public List<Movie> getUserWatchedMovies(Long userId) throws NotFoundException {
        List<Ticket> ticketsByUserId = ticketRepository.findTicketsByUserId(userId);
        if (ticketsByUserId != null && !ticketsByUserId.isEmpty()) {
            List<Movie> distinctByTickets = movieRepository.findDistinctByTickets(ticketsByUserId);
            if (!distinctByTickets.isEmpty())
                return distinctByTickets;
        }

        log.warn("Không tìm thấy phim được xem bởi nguời dùng có ID: {}", userId);
        throw new NotFoundException("Không tìm thấy phim được xem bởi nguời dùng có ID: " + userId);
    }

    @Override
    public List<Movie> recommendMoviesForUser(Long userId) throws NotFoundException {
        List<Movie> userWatchedMovies = getUserWatchedMovies(userId);
        List<Integer> moviesId = userWatchedMovies.stream().map(movie -> movie.getId()).collect(Collectors.toList());

        List<Integer> categoriesId = categoryRepository.getCategoryIdByMovieIdIn(moviesId);

        List<Movie> recommendedMovies = movieRepository.findByCategoryIdIn(categoriesId);
        recommendedMovies.removeIf(userWatchedMovies::contains);
        recommendedMovies = recommendedMovies.subList(0, Math.min(recommendedMovies.size(), 4));
        return recommendedMovies;
    }

}
