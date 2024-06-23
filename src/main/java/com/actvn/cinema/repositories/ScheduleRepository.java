package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Schedule getScheduleById(Integer scheduleId);

    @Query("SELECT s FROM Schedule s WHERE s.startDate >= :startOfDay AND s.startDate <= :endOfDay")
    List<Schedule> findAllByDate(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    @Query("SELECT s FROM Schedule s WHERE s.movie.id=:movieId AND s.room.branch.id=:branchId And s.startDate>=:startOfDay AND s.startDate <= :endOfDay")
    List<Schedule> findByMovie_IdAndBranch_IdAndDate(@Param("movieId") Integer movieId,
                                                     @Param("branchId") Integer branchId, @Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);
}