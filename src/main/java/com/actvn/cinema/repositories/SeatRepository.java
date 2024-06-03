package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> getSeatByRoom_Id(Integer roomId);
}
