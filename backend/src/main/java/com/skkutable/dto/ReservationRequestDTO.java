package com.skkutable.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationRequestDTO {

  private Long userId;
  private Long boothId;
  private Long festivalId;
  private LocalDateTime reservationTime;
  private Integer numberOfPeople;
  private String paymentMethod;

  private String fcmToken;
}
