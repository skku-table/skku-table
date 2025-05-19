package com.skkutable.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationResponseDTO {
    private Long reservationId;
    private Long userId;
    private String userName;
    private Long boothId;
    private String boothName;
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private LocalDateTime createdAt;
}
