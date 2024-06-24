package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Room;

import java.util.List;

public interface RoomService {

    List<Room> listAll() throws NotFoundException;

    void save(Room room) throws IllegalArgumentException;

    void update(Room room) throws IllegalArgumentException;

    Room get(Integer id) throws NotFoundException;

    void delete(Integer id) throws NotFoundException;

    List<Room> findRoomByNameContaining(String name) throws NotFoundException;

    List<Room> getRoomByBranchId(Integer branchId) throws NotFoundException;
}
