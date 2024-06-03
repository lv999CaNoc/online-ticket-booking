package com.actvn.cinema.service;

import com.actvn.cinema.DTO.BookingRequestDTO;
import com.actvn.cinema.exception.BillNotFoundException;
import com.actvn.cinema.model.Bill;
import org.springframework.transaction.annotation.Transactional;

public interface BillService {
    @Transactional
    Integer createNewBill(BookingRequestDTO bookingRequestDTO) throws RuntimeException;

    Bill getBillById(Integer billId) throws BillNotFoundException;
}
