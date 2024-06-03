package com.actvn.cinema.service;

import com.actvn.cinema.exception.RoomNotFoundException;
import com.actvn.cinema.model.Room;

import java.util.List;

public interface RoomService {

    List<Room> listAll();

    void save(Room room);

    Room get(Integer id) throws RoomNotFoundException;

    void delete(Integer id) throws RoomNotFoundException;

    List<Room> findRoomByNameContaining(String name) throws RoomNotFoundException;

    List<Room> getRoomByBranchId(Integer branchId);
}
