package com.actvn.cinema.service;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.ScheduleNotFoundException;
import com.actvn.cinema.model.Schedule;

import java.util.Date;
import java.util.List;

public interface ScheduleService {
    List<Schedule> findByMovie_IdAndBranch_IdAndDate(Integer movieId, Integer branchId, String date);

    List<Schedule> findScheduleByStartDate(Date searchDate);

    List<Schedule> listAll();

    void save(Schedule schedule);

    Schedule get(Integer id) throws ScheduleNotFoundException;

    void delete(Integer id) throws ScheduleNotFoundException;

    List<ResponseSearchScheduleDTO> getSchedule(SearchScheduleDTO search);
}
