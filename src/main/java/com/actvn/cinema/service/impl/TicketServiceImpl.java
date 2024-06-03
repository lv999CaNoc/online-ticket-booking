package com.actvn.cinema.service.impl;

import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired private TicketRepository ticketRepository;

    @Override
    public List<Ticket> getTicketsByUserId(Long userId){
        return ticketRepository.findTicketsByUserId(userId);
    }

    @Override
    public List<Ticket> getTicketsByBillId(Integer billId) {
        return ticketRepository.findTicketsByBill_Id(billId);
    }
}
