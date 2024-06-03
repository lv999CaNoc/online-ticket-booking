package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findTicketsBySchedule_Id(Integer scheduleId);
    @Query("SELECT t FROM Ticket t WHERE t.bill.id IN " +
            "(SELECT b.id FROM Bill b WHERE b.user.id=:userId) " +
            "ORDER BY t.id DESC")
    List<Ticket> findTicketsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t WHERE t.schedule.id=:scheduleId AND t.seat.id=:seatId AND t.bill.status IN (:status)")
    List<Ticket> findTicketsBySchedule_IdAndSeat_IdAAndBill_Status(@Param("scheduleId") Integer scheduleId,
                                                                   @Param("seatId") Long seatId, @Param("status") List<String> status);

    List<Ticket> findTicketsByBill_Id(Integer billId);
}