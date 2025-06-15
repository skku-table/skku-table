package com.skkutable.dto;

import com.skkutable.domain.TimeSlotStatus;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotUpdateDto {

  private LocalDateTime startTime;
  
  private LocalDateTime endTime;
  
  @Min(value = 1, message = "최대 수용 인원은 1명 이상이어야 합니다")
  private Integer maxCapacity;
  
  private TimeSlotStatus status;
}
