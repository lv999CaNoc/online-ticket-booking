package com.actvn.cinema.controller;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.*;
import com.actvn.cinema.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/dashboard")
public class AdminController {
    private MovieService movieService;
    private BranchService branchService;
    private RoomService roomService;
    private ScheduleService scheduleService;
    private UserService userService;
    private ManagerService managerService;
    private BillService billService;

    @GetMapping
    public String getDashboardPage(Model model) {
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

        try {
            List<Movie> top10Movies = movieService.listTop3ByOrderByLikePercentageDesc();
            model.addAttribute("top10Movies", top10Movies);
        } catch (NotFoundException e) {
            model.addAttribute("top10MoviesEmpty", e.getMessage());
        }

        try {
            List<User> listUser = userService.findUserByRole("ROLE_USER");
            model.addAttribute("listUser", listUser);
        } catch (NotFoundException e) {
            model.addAttribute("listUserEmpty", e.getMessage());
        }

        Long cntBills = billService.countBillOfMonth();
        model.addAttribute("cntBill", cntBills);

        model.addAttribute("pageTitle", "Dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/management-movie")
    public String getManagementMoviePage(Model model) {
        try {
            List<Movie> movies = movieService.listAllMovie();
            model.addAttribute("movies", movies);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Quản lý phim");
        return "admin/management-movie";
    }

    @GetMapping("/management-branch")
    public String getManagementBranchPage(Model model) {
        try {
            List<Branch> branches = branchService.listAll();
            model.addAttribute("branches", branches);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Quản lý rạp chiếu");
        return "admin/management-branch";
    }

    @GetMapping("/management-room")
    public String getManagementRoomPage(Model model) {
        try {
            List<Room> rooms = roomService.listAll();
            model.addAttribute("rooms", rooms);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Quản lý phòng");
        return "admin/management-room";
    }

    @GetMapping("/management-schedule")
    public String getManagementSchedulePage(Model model) {
        try {
            List<Schedule> schedules = scheduleService.listAll();
            model.addAttribute("schedules", schedules);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Quản lý lịch chiếu");
        return "admin/management-schedule";
    }

    @GetMapping("/management-manager")
    public String getManagementManagerPage(Model model) {
        try {
            List<Manager> listManager = managerService.listAll();
            model.addAttribute("listManager", listManager);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Manager");
        return "admin/management-manager";
    }

    @GetMapping("/management-user")
    public String getManagementUserPage(Model model) {
        try {
            List<User> listUser = userService.findUserByRole("ROLE_USER");
            model.addAttribute("listUser", listUser);
        } catch (NotFoundException e) {
            model.addAttribute("listUserEmpty", e.getMessage());
        }
        model.addAttribute("pageTitle", "Người dùng");
        return "admin/management-user";
    }
}
