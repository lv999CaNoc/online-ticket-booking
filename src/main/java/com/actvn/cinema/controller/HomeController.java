package com.actvn.cinema.controller;

import com.actvn.cinema.exception.MovieNotFoundException;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.User;
import com.actvn.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired private MovieService movieService;

    @RequestMapping("/")
    public String getHomePage(Model model){
        List<Movie> listMovieNowShowing = movieService.listMovieNowShowing();
        List<Movie> listMovieComingSoon = movieService.listMovieComingSoon();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()){
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                User user = (User) principal;
                List<Movie> recommendMoviesForUser = null;
                try {
                    recommendMoviesForUser = movieService.recommendMoviesForUser(user.getId());
                } catch (MovieNotFoundException e) {
                    System.out.println(e.getMessage());;
                }
                model.addAttribute("recommendMoviesForUser", recommendMoviesForUser);
            }
        }

        model.addAttribute("listMovieNowShowing", listMovieNowShowing);
        model.addAttribute("listMovieComingSoon", listMovieComingSoon);
        return "index";
    }
}
