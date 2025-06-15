package com.skkutable.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDTO {

  @NotNull
  private Long userId;

  @NotNull
  private Long boothId;

  @NotNull
  private Long festivalId;
  
  @NotNull
  private Long timeSlotId;

  @NotNull
  private LocalDateTime reservationTime;

  @NotNull
  @Min(1)
  private Integer numberOfPeople;
  
  private String paymentMethod;

  private String fcmToken;
}
