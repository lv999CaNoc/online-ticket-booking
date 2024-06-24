package com.actvn.cinema.service.impl;

import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);
    @Autowired
    private TicketRepository ticketRepository;

//    TODO: delete
//    @Override
//    public List<Ticket> getTicketsByUserId(Long userId) throws NotFoundException {
//        List<Ticket> tickets = ticketRepository.findTicketsByUserId(userId);
//        if (tickets.isEmpty()) {
//            log.info("[LOG] Không tìm thấy vé được mua bởi người dùng có ID: {}", userId);
//            throw new NotFoundException("Không tìm thấy vé được mua bởi người dùng có ID: "+ userId);
//        }
//        return tickets;
//    }

    @Override
    public List<Ticket> getTicketsByBillId(Integer billId) throws NotFoundException {
        List<Ticket> tickets = ticketRepository.findTicketsByBill_Id(billId);
        if (tickets.isEmpty()) {
            log.info("[LOG] Không tìm thấy vé được mua bởi bill có ID: {}", billId);
            throw new NotFoundException("Không tìm thấy vé được mua bởi bill có ID: " + billId);
        }
        return tickets;
    }
}
