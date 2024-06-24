package com.actvn.cinema.service.impl;

import com.actvn.cinema.DTO.SeatDTO;
import com.actvn.cinema.exception.NotFoundException;
import com.actvn.cinema.model.Room;
import com.actvn.cinema.model.Schedule;
import com.actvn.cinema.model.Seat;
import com.actvn.cinema.model.Ticket;
import com.actvn.cinema.repositories.ScheduleRepository;
import com.actvn.cinema.repositories.SeatRepository;
import com.actvn.cinema.repositories.TicketRepository;
import com.actvn.cinema.service.SeatService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {
    private static final Logger log = LoggerFactory.getLogger(SeatServiceImpl.class);

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SeatDTO> getSeatByScheduleId(Integer scheduleId) throws NotFoundException {
        Schedule schedule = scheduleRepository.getScheduleById(scheduleId);
        if (Objects.isNull(schedule)) {
            log.warn("[LOG] Không tìm thấy lịch chiếu có ID: {}", scheduleId);
            throw new NotFoundException("Không tìm thấy lịch chiếu có ID: " + scheduleId);
        }

        List<Seat> listSeat = seatRepository.getSeatByRoom_Id(schedule.getRoom().getId());

        List<Seat> bookedSeats = new ArrayList<>();
        List<Seat> processedSeats = new ArrayList<>();

        for (Ticket ticket : ticketRepository.findTicketsBySchedule_Id(scheduleId)) {
            String status = ticket.getBill().getStatus();
            if (status.equals("COMPLETE")) {
                bookedSeats.add(ticket.getSeat());
            } else if (status.equals("PENDING")) {
                processedSeats.add(ticket.getSeat());
            }
        }

        List<SeatDTO> filteredSeats = listSeat.stream().map(seat -> {
            SeatDTO seatDTO = modelMapper.map(seat, SeatDTO.class);
            if (bookedSeats.stream()
                    .map(bookedSeat -> bookedSeat.getId())
                    .collect(Collectors.toList()).contains(seat.getId())) {
                seatDTO.setStatus("BOOKED");
            } else if (processedSeats.stream()
                    .map(bookedSeat -> bookedSeat.getId())
                    .collect(Collectors.toList()).contains(seat.getId())) {
                seatDTO.setStatus("PROCESSED");
            }
            return seatDTO;
        }).collect(Collectors.toList());
        return filteredSeats;
    }

    @Override
    public void initSeat(Room room) {
        int SEAT_IN_ROW = 14;
        int numberOfRow = room.getCapacity() / SEAT_IN_ROW;
        for (int i = 0; i < numberOfRow; i++) {
            for (int j = 1; j <= 14; j++) {
                Seat seat = new Seat();
                char param = (char) (i + 'A');
                String name = Character.toString(param) + j;
                seat.setName(name);
                seat.setRoom(room);
                seatRepository.save(seat);
            }
        }
        log.info("[LOG] Khởi tạo ghế cho phòng {} thành công.", room.getName());
    }
}
