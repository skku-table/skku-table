package com.skkutable.dto;

import com.skkutable.domain.TimeSlot;
import com.skkutable.domain.TimeSlotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponseDto {

  private Long id;
  private Long boothId;
  private String boothName;
  private String startTime;
  private String endTime;
  private Integer maxCapacity;
  private Integer currentCapacity;
  private Integer availableCapacity;
  private TimeSlotStatus status;
  private String createdAt;
  private String updatedAt;

  public static TimeSlotResponseDto from(TimeSlot timeSlot) {
    return TimeSlotResponseDto.builder()
        .id(timeSlot.getId())
        .boothId(timeSlot.getBooth().getId())
        .boothName(timeSlot.getBooth().getName())
        .startTime(timeSlot.getStartTime().toString())
        .endTime(timeSlot.getEndTime().toString())
        .maxCapacity(timeSlot.getMaxCapacity())
        .currentCapacity(timeSlot.getCurrentCapacity())
        .availableCapacity(timeSlot.getMaxCapacity() - timeSlot.getCurrentCapacity())
        .status(timeSlot.getStatus())
        .createdAt(timeSlot.getCreatedAt() != null ? timeSlot.getCreatedAt().toString() : null)
        .updatedAt(timeSlot.getUpdatedAt() != null ? timeSlot.getUpdatedAt().toString() : null)
        .build();
  }
}
