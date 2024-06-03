package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.ResponseSearchMovieDTO;
import com.actvn.cinema.DTO.SearchMovieDTO;
import com.actvn.cinema.exception.MovieNotFoundException;
import com.actvn.cinema.model.Category;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Rated;
import com.actvn.cinema.service.CategoryService;
import com.actvn.cinema.service.MovieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MovieController {

    @Autowired private MovieService movieService;
    @Autowired private CategoryService categoryService;

    @GetMapping("/details")
    public String getMovieDetails(@RequestParam("mid") Integer mId,
                                  Model model, RedirectAttributes ra){
        try {
            Movie movie = movieService.getMovieById(mId);
            model.addAttribute("movie", movie);
            model.addAttribute("pageTitle", "Phim "+ movie.getTitle());
            return "movie-details";
        } catch (MovieNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/movie";
        }
    }

    @RequestMapping("/list")
    public String getPageMovieList(Model model){
        List<Movie> listMovieNowShowing = movieService.listMovieNowShowing();
        Rated[] listRated = Rated.values();
        List<Category> listCategories = categoryService.getAll();

        model.addAttribute("movies", listMovieNowShowing);
        model.addAttribute("listRated", listRated);
        model.addAttribute("listCategories", listCategories);
        return "movie-list";
    }
    @GetMapping("/search-advantage")
    public String loadMovieAdvantage(@RequestParam("search") String search
                    , Model model) {
        Rated[] listRated = Rated.values();
        List<Category> listCategories = categoryService.getAll();

        model.addAttribute("listRated", listRated);
        model.addAttribute("listCategories", listCategories);
        List<Movie> movies;
        try {
            movies = movieService.listMovieAdvantage(search);
            model.addAttribute("movies", movies);
        }catch (MovieNotFoundException movieNotFoundException){
            model.addAttribute("error",movieNotFoundException.getMessage());
        }
        return "movie-list";
    }

    @PostMapping("/filter-movie")
    @ResponseBody
    public String filterMovie(@RequestBody SearchMovieDTO searchMovieDTO){
        List<ResponseSearchMovieDTO> movies=new ArrayList<>();
        try {
            for (Movie movie: movieService.filterMovie(searchMovieDTO)){
                System.out.println(movie.getId());
                ResponseSearchMovieDTO responseSearchMovieDTO = new ResponseSearchMovieDTO();
                responseSearchMovieDTO.setId(movie.getId());
                responseSearchMovieDTO.setTitle(movie.getTitle());
                responseSearchMovieDTO.setCategories(movie.getCategories());
                responseSearchMovieDTO.setDescription(movie.getDescription());
                responseSearchMovieDTO.setDuration(movie.getDuration());
                responseSearchMovieDTO.setFormattedReleaseDate(movie.getFormattedReleaseDate());
                responseSearchMovieDTO.setRatedDescription(movie.getRated().getDescription());
                responseSearchMovieDTO.setLikePercentage(movie.getLikePercentage());
                responseSearchMovieDTO.setRevenuePercentage(movie.getRevenuePercentage());
                responseSearchMovieDTO.setTrailerURL(movie.getTrailerURL());
                responseSearchMovieDTO.setMovieImageURl(movie.getMovieImageURl());
                movies.add(responseSearchMovieDTO);
            }
        } catch (MovieNotFoundException e) {
        }
        // Chuyển danh sách thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(movies);
        } catch (JsonProcessingException e) {
        }
        return json;
    }

    @GetMapping("/search")
    public String searchMovieAdmin(@RequestParam("search") String search
            , Model model) throws MovieNotFoundException {
        try{
            List<Movie> movies = movieService.listMovieAdvantage(search);
            model.addAttribute("movies", movies);
        }catch (MovieNotFoundException movieNotFoundException){
            model.addAttribute("errorMessage", movieNotFoundException.getMessage());
        }
        return "admin/management-movie";
    }

    @GetMapping("/new")
    public String showNewForm(Model model){
        List<Category> listCategories = categoryService.getAll();
        Rated[] listRated = Rated.values();

        model.addAttribute("movie", new Movie());
        model.addAttribute("listRated", listRated);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Thêm phim mới");

        return "admin/movie-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id,
                               Model model, RedirectAttributes ra){
        try {
            Movie movie = movieService.getMovieById(id);
            model.addAttribute("movie", movie);

            List<Category> listCategories = categoryService.getAll();
            Rated[] listRated = Rated.values();

            model.addAttribute("listRated", listRated);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Cập nhật phim");
            return "admin/movie-form";
        } catch (MovieNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-movie";
        }
    }

    @PostMapping("/save")
    public String saveMovie(Movie movie, @RequestParam("categories") List<Integer> categoryIds,
                           RedirectAttributes ra){

        // Tạo Set<Category> từ danh sách categoryIds
        Set<Category> categories = new HashSet<>();
        for (Integer categoryId : categoryIds) {
            Category category = categoryService.getCategoryById(categoryId);
            categories.add(category);
        }
        // Gán Set<Category> vào trường categories của đối tượng Movie
        movie.setCategories(categories);

        movieService.save(movie);
        ra.addFlashAttribute("successMessage", "Lưu thông tin phim thành công.");
        return "redirect:/dashboard/management-movie";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer movieId,
                         RedirectAttributes ra){
        try {
            movieService.deleteById(movieId);
            ra.addFlashAttribute("successMessage","Xóa phim có id = "+movieId+" thành công.");
        } catch (MovieNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-movie";
    }
}
