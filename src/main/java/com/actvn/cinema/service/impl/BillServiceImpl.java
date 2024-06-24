package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.BookingRequestDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Bill;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.model.User;
import com.actvn.cinema.repositories.*;
import com.actvn.cinema.service.BillService;
import com.actvn.cinema.service.ImgbbService;
import com.actvn.cinema.service.QRCodeService;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BillServiceImpl implements BillService {

    private TicketRepository ticketRepository;
    private SeatRepository seatRepository;
    private BillRepository billRepository;
    private ScheduleRepository scheduleRepository;
    private UserRepository userRepository;
    private QRCodeService qrCodeService;
    private ImgbbService uploadService;

    @Override
    @Transactional
    public Integer createNewBill(BookingRequestDTO bookingRequestDTO) throws RuntimeException {
        User user = userRepository.getById(bookingRequestDTO.getUserId());
        Schedule schedule = scheduleRepository.getById(bookingRequestDTO.getScheduleId());

        // create bill
        Bill billToCreate = new Bill();
        billToCreate.setUser(user);
        billToCreate.setCreatedTime(LocalDateTime.now());
        billToCreate.setStatus("PENDING");
        Bill createdBill = billRepository.save(billToCreate);

        List<Ticket> tickets = new ArrayList<>();
        // create ticket for each seat
        bookingRequestDTO.getListSeatIds().forEach(seatId -> {
            if (ticketRepository.findTicketsBySchedule_IdAndSeat_IdAAndBill_Status(schedule.getId(), seatId, Arrays.asList("COMPLETE", "PENDING")).isEmpty()) {
                Ticket ticketToCreate = new Ticket();
                ticketToCreate.setSchedule(schedule);
                ticketToCreate.setBill(createdBill);
                ticketToCreate.setQrImageURL("");
                ticketToCreate.setSeat(seatRepository.getById(seatId));
                ticketRepository.save(ticketToCreate);
                tickets.add(ticketToCreate);
            } else {
                // someone else booked
                throw new RuntimeException("Đã có người đặt ghế trước bạn.");
            }
        });
        // generate qr code
        for (Ticket ticket : tickets) {
            String qrContext = "UserID: " + user.getId() + "BillID: " + billToCreate.getId() + ", TicketID: " + ticket.getId();

            try {
                byte[] imageBytes = qrCodeService.generateQRCodeImage(qrContext, 240, 240);
                HttpResponse<String> response = uploadService.upload(imageBytes);
                if (response.statusCode() == 200) {
                    String qrImageUrl = uploadService.getDisplayUrl(response.body());
                    System.out.println("Image uploaded successfully.\n");

                    ticket.setQrContext(qrContext);
                    ticket.setQrImageURL(qrImageUrl);

                    ticketRepository.save(ticket);
                } else {
                    System.err.println("Failed to upload image. Status code: " + response.statusCode());
                }
            } catch (WriterException | IOException | InterruptedException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        return createdBill.getId();
    }

    @Override
    public Bill getBillById(Integer id) throws NotFoundException {
        Optional<Bill> result = billRepository.findById(id);
        if (result.isPresent()) return result.get();

        throw new NotFoundException("Không tìm bill với id: " + id);
    }

    @Override
    public Long countBillOfMonth() {
        return billRepository.countOfMonth();
    }
}
