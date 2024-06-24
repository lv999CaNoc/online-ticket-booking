package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.repositories.RoomRepository;
import com.actvn.cinema.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private Validator validator;
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> listAll() throws NotFoundException {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            log.warn("Không có phòng");
            throw new NotFoundException("Không có phòng");
        }
        return rooms;
    }

    @Override
    public void save(Room room) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(room, "room");

        // Kiểm tra phòng có trùng tên? có trong cùng 1 chi nhánh --> loại
        List<Room> rooms = roomRepository.findRoomByNameContainingIgnoreCase(room.getName());
        for (Room r: rooms){
            if (r.getBranch().equals(room.getBranch())){
                    errors.rejectValue("name", "name.exists", "Tên phòng đã tồn tại trong rạp. Vui lòng thử lại.");
                    break;
            }
        }

        validator.validate(room, errors);

        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
        }
        log.info("[LOG] Lưu phòng thành công");
        roomRepository.save(room);
    }

    @Override
    public void update(Room room) throws IllegalArgumentException {
        Errors errors = new BeanPropertyBindingResult(room, "room");

        // Kiểm tra phòng có trùng tên? có trong cùng 1 chi nhánh --> loại
        List<Room> rooms = roomRepository.findRoomByNameContainingIgnoreCase(room.getName());
        for (Room r: rooms){
            if(room.getId() != r.getId() && r.getBranch().equals(room.getBranch())){
                errors.rejectValue("name", "name.exists", "Tên phòng đã tồn tại trong rạp. Vui lòng thử lại.");
                break;
            }
        }

        validator.validate(room, errors);

        if (errors.hasErrors()) {
            String message = errors.getAllErrors().get(0).getDefaultMessage();
            log.warn("[LOG] Validation errors: {}", message);
            throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
        }
        log.info("[LOG] Cập nhật phòng thành công");
        roomRepository.save(room);
    }

    @Override
    public Room get(Integer id) throws NotFoundException {
        Optional<Room> result = roomRepository.findById(id);
        if (result.isPresent())
            return result.get();

        log.warn("[LOG] Không tìm thấy phòng chiếu với id: {}", id);
        throw new NotFoundException("Không tìm thấy phòng chiếu với id = " + id);
    }

    @Override
    public void delete(Integer id) throws NotFoundException {
        Optional<Room> result = roomRepository.findById(id);
        if (result.isPresent()) {
            log.info("[LOG] Tìm thấy phòng chiếu với id: {} thành công", id);
            roomRepository.deleteById(id);
        } else {
            log.warn("[LOG] Không tìm thấy phòng chiếu với id: {}", id);
            throw new NotFoundException("Không tìm thấy phòng chiếu với id: " + id);
        }
    }

    @Override
    public List<Room> findRoomByNameContaining(String name) throws NotFoundException {
        List<Room> roomByName = roomRepository.findRoomByNameContainingIgnoreCase(name);
        if (roomByName.isEmpty()) {
            log.warn("[LOG] Không tìm thấy phòng có tên chứa: {}", name);
            throw new NotFoundException("Không tìm thấy phòng có tên chứa " + name);
        }
        return roomByName;
    }

    @Override
    public List<Room> getRoomByBranchId(Integer branchId) throws NotFoundException {
        List<Room> rooms = roomRepository.findRoomByBranch_Id(branchId);
        if (rooms.isEmpty()) {
            log.warn("Không tìm thấy phòng trong chi nhánh có ID: {}", branchId);
            throw new NotFoundException("Không tìm thấy phòng trong chi nhánh có ID: " + branchId);
        }
        return rooms;
    }
}
