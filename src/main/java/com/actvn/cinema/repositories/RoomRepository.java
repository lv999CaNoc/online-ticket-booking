package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findRoomByNameContainingIgnoreCase(String name);

    List<Room> findRoomByBranch_Id(Integer branchId);
}