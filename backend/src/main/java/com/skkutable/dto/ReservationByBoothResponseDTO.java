package com.skkutable.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ReservationByBoothResponseDTO {

  private BoothInfo booth;
  private List<UserReservationInfo> reservations;

  @Data
  public static class BoothInfo {

    private Long id;
    private String name;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String posterImageUrl;
    private int likeCount;
  }

  @Data
  public static class UserReservationInfo {

    private Long reservationId;
    private Long userId;
    private String userName;
    private Long timeSlotId;
    private LocalDateTime timeSlotStartTime;
    private LocalDateTime timeSlotEndTime;
    private int numberOfPeople;
    private String paymentMethod;
    private LocalDateTime createdAt;
  }
}
