package com.actvn.cinema.service;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Schedule;

import java.util.Date;
import java.util.List;

public interface ScheduleService {

    List<Schedule> findScheduleByStartDate(Date searchDate) throws NotFoundException;

    List<Schedule> listAll() throws NotFoundException;

    void save(Schedule schedule) throws IllegalArgumentException;

    Schedule get(Integer id) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<ResponseSearchScheduleDTO> search(SearchScheduleDTO search);
}
