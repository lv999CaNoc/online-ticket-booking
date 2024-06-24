package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {
    private MovieService movieService;

    @RequestMapping("/")
    public String getHomePage(Model model) {
        try {
            List<Movie> listMovieNowShowing = movieService.listMovieNowShowing();
            model.addAttribute("listMovieNowShowing", listMovieNowShowing);
        } catch (NotFoundException e) {
            model.addAttribute("listMovieNowShowingEmpty", e.getMessage());
        }

        try {
            List<Movie> listMovieComingSoon = movieService.listMovieComingSoon();
            model.addAttribute("listMovieComingSoon", listMovieComingSoon);
        } catch (NotFoundException e) {
            model.addAttribute("listMovieComingSoonEmpty", e.getMessage());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                List<Movie> recommendMoviesForUser = null;
                try {
                    recommendMoviesForUser = movieService.recommendMoviesForUser(user.getId());
                } catch (NotFoundException e) {
                    System.out.println(e.getMessage());
                }
                model.addAttribute("recommendMoviesForUser", recommendMoviesForUser);
            }
        }

        return "index";
    }
}
