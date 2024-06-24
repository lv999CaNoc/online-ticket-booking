package com.actvn.cinema.service;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Ticket;

import java.util.List;

public interface TicketService {
//    TODO: delete
//    List<Ticket> getTicketsByUserId(Long userId) throws NotFoundException;

    List<Ticket> getTicketsByBillId(Integer billId) throws NotFoundException;
}
