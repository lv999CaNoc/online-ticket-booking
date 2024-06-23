package com.actvn.cinema.repositories;

import com.actvn.cinema.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> findByCreatedTimeBeforeAndStatus(LocalDateTime time, String status);

    @Query("SELECT COUNT(b) FROM Bill b WHERE MONTH(b.createdTime) = MONTH(CURRENT_DATE) AND YEAR(b.createdTime) = YEAR(CURRENT_DATE)")
    Long countOfMonth();
}
