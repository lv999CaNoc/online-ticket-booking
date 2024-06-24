package com.actvn.cinema.controller;

import com.actvn.cinema.DTO.BookingRequestDTO;
import com.actvn.cinema.DTO.BookingResponseDTO;
import com.actvn.cinema.DTO.SeatDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.repositories.UserRepository;
import com.actvn.cinema.service.BillService;
import com.actvn.cinema.service.ScheduleService;
import com.actvn.cinema.service.SeatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private BillService billService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/plan")
    public String getMovieSeatPlan(@RequestParam("sid") Integer scheduleId, Model model) {
        try {
            Schedule schedule = scheduleService.get(scheduleId);
            model.addAttribute("schedule", schedule);
            model.addAttribute("movie", schedule.getMovie());
            model.addAttribute("room", schedule.getRoom());
        } catch (NotFoundException e) {
            model.addAttribute("notFound", e.getMessage());
            return "error/404";
        }
        return "seat-plan";
    }

    @PostMapping("/loadSeat")
    @ResponseBody
    public String loadSeat(@RequestBody BookingRequestDTO bookingRequestDTO) {
        List<SeatDTO> response = null;
        try {
            response = seatService.getSeatByScheduleId(bookingRequestDTO.getScheduleId());
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
        }
        // Chuyển danh sách thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.err.println(e.getMessage());
        }
        return json;
    }

    @PostMapping("/reserveSeats")
    @ResponseBody
    public String reserveSeats(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO response = new BookingResponseDTO();
        try {
            response.setBillId(billService.createNewBill(bookingRequestDTO));
            response.setSuccess(true);
            System.out.println("Book seat: success");
        } catch (RuntimeException e) {
            response.setSuccess(false);
            System.out.println("Book seat: fail");
        }
        // Chuyển danh sách thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return json;
    }
}
