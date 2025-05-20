package com.skkutable.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReservationResponseDTO {
    private Long reservationId;
    private Long userId;
    private String userName;
    private String festivalName;
    private Date festivalStartDate;
    private String festivalPosterImageUrl;
    private Long boothId;
    private String boothName;
    private LocalDateTime reservationTime;
    private int numberOfPeople;
    private LocalDateTime createdAt;
}
