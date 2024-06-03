package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.RoomNotFoundException;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.repositories.RoomRepository;
import com.actvn.cinema.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired private RoomRepository roomRepository;
    @Override
    public List<Room> listAll(){
        return (List<Room>) roomRepository.findAll();
    }
    @Override
    public void save(Room room){
        roomRepository.save(room);
    }
    @Override
    public Room get(Integer id) throws RoomNotFoundException {
        Optional<Room> result =roomRepository.findById(id);
        if (result.isPresent())
            return result.get();
        throw new RoomNotFoundException("Không tìm thấy phòng chiếu với id = " +id);
    }
    @Override
    public void delete(Integer id) throws RoomNotFoundException {
        Optional<Room> result = roomRepository.findById(id);
        if (result.isPresent())
            roomRepository.deleteById(id);
        else throw new RoomNotFoundException("Không tìm thấy phòng chiếu với id = " +id);
    }

    @Override
    public List<Room> findRoomByNameContaining(String name) throws RoomNotFoundException {
        List<Room> roomByName = roomRepository.findRoomByNameContainingIgnoreCase(name);
        if (roomByName.isEmpty()) {
            throw new RoomNotFoundException("Không tìm thấy phòng có tên chứa "+name);
        }
        return roomByName;
    }

    @Override
    public List<Room> getRoomByBranchId(Integer branchId){
        return roomRepository.findRoomByBranch_Id(branchId);
    }
}
