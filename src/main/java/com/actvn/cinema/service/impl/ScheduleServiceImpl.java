package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.ScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.ScheduleNotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.repositories.BranchRepository;
import com.actvn.cinema.repositories.ScheduleRepository;
import com.actvn.cinema.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private BranchRepository branchRepository;

    @Override
    public List<Schedule> findByMovie_IdAndBranch_IdAndDate(Integer movieId, Integer branchId, String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date searchDate = null;
        try {
            searchDate = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        return scheduleRepository.findByMovie_IdAndBranch_IdAndDate(movieId,branchId,startOfDay,endOfDay);
    }

    @Override
    public List<Schedule> findScheduleByStartDate(Date searchDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();
        return scheduleRepository.findAllByDate(startOfDay, endOfDay);
    }

    @Override
    public List<Schedule> listAll(){
        return (List<Schedule>) scheduleRepository.findAll();
    }
    @Override
    public void save(Schedule schedule){
        scheduleRepository.save(schedule);
    }
    @Override
    public Schedule get(Integer id) throws ScheduleNotFoundException {
        Optional<Schedule> result =scheduleRepository.findById(id);
        if (result.isPresent())
            return result.get();
        throw new ScheduleNotFoundException("Không tìm thấy lịch chiếu với id = " +id);
    }
    @Override
    public void delete(Integer id) throws ScheduleNotFoundException {
        Optional<Schedule> result = scheduleRepository.findById(id);
        if (result.isPresent())
            scheduleRepository.deleteById(id);
        throw new ScheduleNotFoundException("Không tìm thấy lịch chiếu với id = " +id);
    }

    @Override
    public List<ResponseSearchScheduleDTO> getSchedule(SearchScheduleDTO search) {
        List<ResponseSearchScheduleDTO> response = new ArrayList<>();
        List<String> cities = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (search.getCity().isEmpty()){
            cities = branchRepository.getAllThanhPho();
        }else{
            cities.add(search.getCity());
        }

        if (search.getBranchId()==null){
            if (search.getCity().isEmpty()){
                branches= branchRepository.findAll();
            }else{
                branches=branchRepository.findBranchByThanhPho(search.getCity());
            }
        }else{
            branches.add(branchRepository.getById(search.getBranchId()));
        }
        Date searchDate = null;
        try {
            searchDate = dateFormat.parse(search.getDate());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startOfDay = calendar.getTime();
        Date now = new Date();
        if (startOfDay.before(now)) startOfDay=now;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();
        for (Branch branch: branches){
            ResponseSearchScheduleDTO re = new ResponseSearchScheduleDTO();
            re.setBranchId(branch.getId());
            re.setBranchName(branch.getName());
            List<ScheduleDTO> dtoList = new ArrayList<>();
            for (Schedule schedule: scheduleRepository.findByMovie_IdAndBranch_IdAndDate(search.getMovieId(), branch.getId(), startOfDay, endOfDay)){
                ScheduleDTO dto = new ScheduleDTO();
                dto.setScheduleId(schedule.getId());
                dto.setStartTime(timeFormat.format(schedule.getStartDate()));
                dtoList.add(dto);
            }
            re.setListSchedule(dtoList);
            response.add(re);
        }
        return response;
    }
}
