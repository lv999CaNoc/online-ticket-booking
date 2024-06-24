package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Movie;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.service.BranchService;
import com.actvn.cinema.service.MovieService;
import com.actvn.cinema.service.RoomService;
import com.actvn.cinema.service.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private BranchService branchService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private MovieService movieService;

    @GetMapping("/search")
    public String search(@RequestParam("search") @DateTimeFormat(pattern = "dd/MM/yyyy") Date searchDate,
                         Model model) {
        try {
            List<Schedule> schedules = scheduleService.findScheduleByStartDate(searchDate);
            model.addAttribute("schedules", schedules);
        } catch (NotFoundException e) {
            model.addAttribute("errorEmpty", e.getMessage());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        model.addAttribute("pageTitle", formatter.format(searchDate));
        return "admin/management-schedule";
    }

    @GetMapping("/new")
    public String showNewForm(Model model, RedirectAttributes ra) {
        try {
            Branch branch = branchService.get(1);
            List<Room> listRoom = roomService.getRoomByBranchId(branch.getId());
            List<Movie> listMovie = movieService.listMovieNowShowing();

            model.addAttribute("branch", branch);
            model.addAttribute("listRoom", listRoom);
            model.addAttribute("listMovie", listMovie);
            model.addAttribute("schedule", new Schedule());
            model.addAttribute("pageTitle", "Thêm lịch chiếu mới");
            return "admin/schedule-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-schedule";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Schedule schedule = scheduleService.get(id);

            Branch branch = schedule.getRoom().getBranch();

            List<Room> listRoom = roomService.getRoomByBranchId(branch.getId());
            List<Movie> listMovie = movieService.listMovieNowShowing();

            model.addAttribute("branch", branch);
            model.addAttribute("listRoom", listRoom);
            model.addAttribute("listMovie", listMovie);

            model.addAttribute("schedule", schedule);

            model.addAttribute("pageTitle", "Cập nhật lịch chiếu");
            return "admin/schedule-form";
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard/management-schedule";
        }
    }

    @PostMapping("/save")
    public String save(Schedule schedule, RedirectAttributes ra) {
        try {
            scheduleService.save(schedule);
            ra.addFlashAttribute("successMessage", "Lưu lịch chiếu thành công.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-schedule";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            scheduleService.delete(id);
            ra.addFlashAttribute("successMessage", "Xóa lịch chiếu thành công.");
        } catch (NotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard/management-schedule";
    }

    @PostMapping("/loadData")
    @ResponseBody
    public String loadData(@RequestBody SearchScheduleDTO search) throws JsonProcessingException {
        List<ResponseSearchScheduleDTO> response = scheduleService.search(search);
        // Chuyển danh sách thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);
        return json;
    }
}
