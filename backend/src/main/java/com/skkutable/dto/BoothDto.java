package com.skkutable.dto;

import com.skkutable.domain.Festival;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class BoothDto {

  @NotNull
  private Long festivalId;

  @NotBlank
  private String name;

  @NotBlank
  private String host;

  private String location;
  private String description;

  @NotNull
  private LocalDateTime startDateTime;

  @NotNull
  private LocalDateTime endDateTime;

  private String posterImageUrl;
  private String eventImageUrl;
}
