package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.ResponseSearchMovieDTO;
import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Category;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Rated;
import com.actvn.cinema.service.CategoryService;
import com.actvn.cinema.service.MovieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/movie")
@AllArgsConstructor
public class MovieController {

    private MovieService movieService;
    private CategoryService categoryService;

    @GetMapping("/details")
    public String getMovieDetails(@RequestParam("mid") Integer mId, Model model) {
        try {
            Movie movie = movieService.getMovieById(mId);
            model.addAttribute("movie", movie);
            model.addAttribute("pageTitle", "Phim " + movie.getTitle());
            return "movie-details";
        } catch (NotFoundException e) {
            model.addAttribute("notFound", e.getMessage());
            return "error/404";
        }
    }

    @RequestMapping("/list")
    public String getPageMovieList(Model model) {
        try {
            List<Movie> listMovieNowShowing = movieService.listMovieNowShowing();
            model.addAttribute("movies", listMovieNowShowing);
        } catch (NotFoundException e) {
            model.addAttribute("notFoundMovie", e.getMessage());
        }

        Rated[] listRated = Rated.values();
        model.addAttribute("listRated", listRated);

        try {
            List<Category> listCategories = categoryService.getAll();
            model.addAttribute("listCategories", listCategories);
        } catch (NotFoundException e) {
            model.addAttribute("errorCategoryEmpty", e.getMessage());
        }

        return "movie-list";
    }

    @GetMapping("/search-advantage")
    public String searchMovie(@RequestParam("search") String search, Model model) {
        Rated[] listRated = Rated.values();
        model.addAttribute("listRated", listRated);

        try {
            List<Category> listCategories = categoryService.getAll();
            model.addAttribute("listCategories", listCategories);
        } catch (NotFoundException e) {
            model.addAttribute("errorCategoryEmpty", e.getMessage());
        }

        try {
            List<Movie> movies = movieService.search(search);
            model.addAttribute("movies", movies);
        } catch (NotFoundException e) {
            model.addAttribute("notFoundMovie", e.getMessage());
        }
        return "movie-list";
    }

    @PostMapping("/filter-movie")
    @ResponseBody
    public String filterMovie(@RequestBody SearchMovieDTO searchMovieDTO) {
        List<ResponseSearchMovieDTO> movies = new ArrayList<>();
        try {
            for (Movie movie : movieService.filterMovie(searchMovieDTO)) {
                ResponseSearchMovieDTO responseSearchMovieDTO = new ResponseSearchMovieDTO(
                        movie.getId(), movie.getTitle(), movie.getCategories(), movie.getDescription(),
                        movie.getDuration(), movie.getFormattedReleaseDate(), movie.getRated().getDescription(),
                        movie.getLikePercentage(), movie.getRevenuePercentage(), movie.getTrailerURL(), movie.getMovieImageURl()
                );
                movies.add(responseSearchMovieDTO);
            }
        } catch (NotFoundException e) {
            // Giấu bug =)))
        }
        // Chuyển danh sách thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(movies);
        } catch (JsonProcessingException e) {
            // Giấu bug =)))
        }
        return json;
    }

    // For manager & admin
    @GetMapping("/search")
    public String searchMovieAdmin(@RequestParam("search") String search
            , Model model) {
        try {
            List<Movie> movies = movieService.search(search);
            model.addAttribute("movies", movies);
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "admin/management-movie";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        try {
            List<Category> listCategories = categoryService.getAll();
            model.addAttribute("listCategories", listCategories);
        } catch (NotFoundException e) {
            model.addAttribute("listCategoriesEmpty", e.getMessage());
        }

        Rated[] listRated = Rated.values();

        model.addAttribute("movie", new Movie());
        model.addAttribute("listRated", listRated);
        model.addAttribute("pageTitle", "Thêm phim mới");

        return "admin/movie-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Movie movie = movieService.getMovieById(id);
            model.addAttribute("movie", movie);

            List<Category> listCategories = categoryService.getAll();
            Rated[] listRated = Rated.values();

            model.addAttribute("listRated", listRated);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Cập nhật phim");
            return "admin/movie-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-movie";
        }
    }

    @PostMapping("/save")
    public String saveMovie(Movie movie, @RequestParam("categories") List<Integer> categoryIds, RedirectAttributes ra) {
        try {
            // Tạo Set<Category> từ danh sách categoryIds
            Set<Category> categories = new HashSet<>();
            for (Integer categoryId : categoryIds) {
                Category category = categoryService.getCategoryById(categoryId);
                categories.add(category);
            }
            movie.setCategories(categories);

            movieService.save(movie);
        } catch (NotFoundException | IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/movie/new";
        }
        ra.addFlashAttribute("successMessage", "Lưu thông tin phim thành công.");
        return "redirect:/dashboard/management-movie";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer movieId, RedirectAttributes ra) {
        try {
            movieService.deleteById(movieId);
            ra.addFlashAttribute("successMessage", "Xóa phim có id = " + movieId + " thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-movie";
    }
}
