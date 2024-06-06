package com.actvn.cinema.controller;

import com.actvn.cinema.model.*;
import com.actvn.cinema.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class AdminController {
    @Autowired private MovieService movieService;
    @Autowired private BranchService branchService;
    @Autowired private RoomService roomService;
    @Autowired private ScheduleService scheduleService;
    @Autowired private UserService userService;
    @Autowired private ManagerService managerService;
    @Autowired private BillService billService;

    @GetMapping
    public String getDashboardPage(Model model){
        List<Movie> listMovieNowShowing = movieService.listMovieNowShowing();
        List<Movie> listMovieComingSoon = movieService.listMovieComingSoon();

        List<Movie> top10Movies = movieService.listTop3ByOrderByLikePercentageDesc();

        Long cntBills = billService.countBillOfMonth();

        List<User> listUser = userService.findUserByRole("ROLE_USER");

        model.addAttribute("pageTitle","Dashboard");
        model.addAttribute("cntBill",cntBills);

        model.addAttribute("listMovieNowShowing",listMovieNowShowing);
        model.addAttribute("listMovieComingSoon",listMovieComingSoon);
        model.addAttribute("top10Movies",top10Movies);
        model.addAttribute("listUser",listUser);

        return "admin/dashboard";
    }

    @GetMapping("/management-movie")
    public String getManagementMoviePage(Model model){
        List<Movie> movies = movieService.listAllMovie();

        model.addAttribute("movies", movies);
        model.addAttribute("pageTitle","Quản lý phim");
        return "admin/management-movie";
    }
    @GetMapping("/management-branch")
    public String getManagementBranchPage(Model model){
        List<Branch> branches = branchService.listAll();
        model.addAttribute("branches", branches);
        model.addAttribute("pageTitle","Quản lý rạp chiếu");
        return "admin/management-branch";
    }

    @GetMapping("/management-room")
    public String getManagementRoomPage(Model model){
        List<Room> rooms = roomService.listAll();
        model.addAttribute("rooms", rooms);
        model.addAttribute("pageTitle","Quản lý phòng");
        return "admin/management-room";
    }

    @GetMapping("/management-schedule")
    public String getManagementSchedulePage(Model model){
        List<Schedule> schedules = scheduleService.listAll();
        model.addAttribute("schedules", schedules);
        model.addAttribute("pageTitle","Quản lý lịch chiếu");
        return "admin/management-schedule";
    }

    @GetMapping("/management-manager")
    public String getManagementManagerPage(Model model){
        List<Manager> listManager = managerService.listAll();
        model.addAttribute("listManager", listManager);
        model.addAttribute("pageTitle","Manager");
        return "admin/management-manager";
    }

    @GetMapping("/management-user")
    public String getManagementUserPage(Model model){
        List<User> listUser = userService.findUserByRole("ROLE_USER");
        model.addAttribute("listUser", listUser);
        model.addAttribute("pageTitle","User");
        return "admin/management-user";
    }
}
