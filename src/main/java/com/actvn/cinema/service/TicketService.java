package com.actvn.cinema.service;

import com.actvn.cinema.model.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> getTicketsByUserId(Long userId);

    List<Ticket> getTicketsByBillId(Integer billId);
}
