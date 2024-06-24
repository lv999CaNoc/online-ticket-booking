package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.model.User;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private MovieService movieService;
    @Autowired
    private BranchService branchService;

    @GetMapping("/plan")
    public String getMovieTicketPlan(@RequestParam("mid") Integer movieId, Model model) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            model.addAttribute("movie", movie);
            List<Branch> listBranch = branchService.listAll();
            model.addAttribute("listBranch", listBranch);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
            model.addAttribute("notFound", e.getMessage());
            return "error/404";
        }

        List<String> dates = get7Date();
        model.addAttribute("dates", dates);
        model.addAttribute("searchSchedule", new SearchScheduleDTO());
        return "ticket-plan";
    }

    private List<String> get7Date() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            String formattedDate = formatter.format(date);
            dates.add(formattedDate);
        }
        return dates;
    }

    @GetMapping("/history")
    public String bookingHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                List<Ticket> ticketsByUserId = ticketRepository.findTicketsByUserId(user.getId());
                model.addAttribute("tickets", ticketsByUserId);
            }
        }
        model.addAttribute("pageTitle", "Lịch sử mua vé");
        return "booking-history";
    }
}
