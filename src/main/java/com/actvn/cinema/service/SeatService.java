package com.actvn.cinema.service;

import com.actvn.cinema.DTO.SeatDTO;
import com.actvn.cinema.model.Room;

import java.util.List;

public interface SeatService {
    List<SeatDTO> getSeatByScheduleId(Integer scheduleId);

    void initSeat(Room room);
}
