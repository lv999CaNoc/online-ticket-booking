package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.ResponseSearchScheduleDTO;
import com.actvn.cinema.DTO.ScheduleDTO;
import com.actvn.cinema.DTO.SearchScheduleDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Branch;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.repositories.BranchRepository;
import com.actvn.cinema.repositories.ScheduleRepository;
import com.actvn.cinema.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private Validator validator;

    @Override
    public List<Schedule> findScheduleByStartDate(Date searchDate) throws NotFoundException {
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

        List<Schedule> schedules = scheduleRepository.findAllByDate(startOfDay, endOfDay);
        if (schedules.isEmpty()){
            log.warn("[LOG] Không tìm thấy lịch chiếu thoả mãn ngày chiếu: {}", searchDate);
            throw new NotFoundException("Không tìm thấy lịch chiếu thoả mãn ngày chiếu: "+searchDate);
        }
        log.info("[Tìm lịch chiếu thoả mãn ngày chiếu {} thành công.", searchDate);
        return schedules;
    }

    @Override
    public List<Schedule> listAll() throws NotFoundException {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            log.warn("[LOG] Không có lịch chiếu");
            throw new NotFoundException("Không có lịch chiếu");
        }
        return schedules;
    }

    @Override
    public void save(@Valid Schedule schedule) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(schedule, "schedule");
        validator.validate(schedule, errors);
        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
        }
        scheduleRepository.save(schedule);
        log.info("[LOG] Lưu lịch chiếu thành công");
    }

    @Override
    public Schedule get(Integer id) throws NotFoundException {
        Optional<Schedule> result = scheduleRepository.findById(id);
        if (result.isPresent())
            return result.get();

        log.warn("[LOG] Không tìm thấy lịch chiếu với id: {}", id);
        throw new NotFoundException("Không tìm thấy lịch chiếu với id: " + id);
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        Optional<Schedule> result = scheduleRepository.findById(id);
        if (result.isPresent()) {
            log.info("[LOG] Xoá thành công lịch chiếu có ID: ", id);
            scheduleRepository.deleteById(id);
        } else {
            log.warn("[LOG] Không tìm thấy lịch chiếu có ID: {}", id);
            throw new NotFoundException("Không tìm thấy lịch chiếu có ID:" + id);
        }

    }

    @Override
    public List<ResponseSearchScheduleDTO> search(SearchScheduleDTO search) {
        List<ResponseSearchScheduleDTO> response = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        if (search.getBranchId() == null) {
            // Nếu không chọn cụ thể chi nhánh? -> lấy theo thành phố
            if (search.getCity().isEmpty()) {
                // Nếu không chọn cụ thể thành phố -> lấy tất cả chi nhánh
                branches = branchRepository.findAll();
            } else {
                // lấy chi nhánh theo thành phố
                branches = branchRepository.findBranchByThanhPho(search.getCity());
            }
        } else {
            // lấy chi nhánh được chọn
            branches.add(branchRepository.getById(search.getBranchId()));
        }

        Date searchDate = null;
        try {
            searchDate = dateFormat.parse(search.getDate());
        } catch (ParseException e) {
            log.warn("[LOG] {}", e.getMessage());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(searchDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date startOfDay = calendar.getTime();
        Date now = new Date();
        if (startOfDay.before(now)) startOfDay = now;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        for (Branch branch : branches) {
            ResponseSearchScheduleDTO re = new ResponseSearchScheduleDTO();
            re.setBranchId(branch.getId());
            re.setBranchName(branch.getName());
            List<ScheduleDTO> dtoList = new ArrayList<>();
            for (Schedule schedule : scheduleRepository.findByMovie_IdAndBranch_IdAndDate(search.getMovieId(), branch.getId(), startOfDay, endOfDay)) {
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
