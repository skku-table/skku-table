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
public class TimeSlotCreateDto {

  @NotNull(message = "시작 시간은 필수입니다")
  private LocalDateTime startTime;

  @NotNull(message = "종료 시간은 필수입니다")
  private LocalDateTime endTime;

  @NotNull(message = "최대 수용 인원은 필수입니다")
  @Min(value = 1, message = "최대 수용 인원은 1명 이상이어야 합니다")
  private Integer maxCapacity;
}
