package com.actvn.cinema.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@Table(name = "schedule")
@Entity
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @DateTimeFormat(pattern = "HH:mm dd/MM/yyyy")
    private Date startDate;
    private double price;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public boolean isComing(){
        Date now = new Date();
        return startDate.after(now);
    }

    public boolean isOpen(){
        Date now = new Date();
        LocalTime timeNow = LocalDateTime.ofInstant(now.toInstant(),
                ZoneId.systemDefault()).toLocalTime();
        LocalTime startTime = LocalDateTime.ofInstant(startDate.toInstant(),
                ZoneId.systemDefault()).toLocalTime();
        LocalTime endTime = startTime.plusHours(2);
        return startDate.before(now) && endTime.isAfter(timeNow);
    }

}
