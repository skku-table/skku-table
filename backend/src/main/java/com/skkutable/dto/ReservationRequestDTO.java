package com.skkutable.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequestDTO {
    private Long userId;
    private Long boothId;
    private Long festivalId;
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private String paymentMethod;
}
