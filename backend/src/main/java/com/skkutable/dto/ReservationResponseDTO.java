package com.skkutable.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationResponseDTO {

  private Long reservationId;
  private Long userId;
  private String userName;
  private Long festivalId;
  private String festivalName;
  private Long boothId;
  private String boothName;
  private LocalDateTime boothStartDate;
  private String boothPosterImageUrl;
  private int numberOfPeople;
  private String paymentMethod;
  private LocalDateTime createdAt;

  // TimeSlot 정보
  private Long timeSlotId;
  private LocalDateTime timeSlotStartTime;
  private LocalDateTime timeSlotEndTime;
}