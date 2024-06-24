package com.actvn.cinema.service;

import com.actvn.cinema.DTO.SeatDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Room;

import java.util.List;

public interface SeatService {
    List<SeatDTO> getSeatByScheduleId(Integer scheduleId) throws NotFoundException;

    void initSeat(Room room);
}
