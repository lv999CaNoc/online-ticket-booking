package com.actvn.cinema.config;

import com.actvn.cinema.model.Bill;
import com.actvn.cinema.repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class BillCleanupTask {
    @Autowired private BillRepository billRepository;

    @Scheduled(fixedRate = 5*60000) // Chạy mỗi 5 phút
    public void billCleanupTask() {
        System.out.println("billCleanupTask: Cleanup Bill running...");
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Bill> expiredBills = billRepository.findByCreatedTimeBeforeAndStatus(fiveMinutesAgo, "PENDING");
        for (Bill bill: expiredBills) {
            bill.setStatus("CANCEL");
        }
        billRepository.saveAll(expiredBills);
    }
}
