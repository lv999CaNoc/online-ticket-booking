package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> getTicketsByBillId(Integer billId) throws NotFoundException;
}
