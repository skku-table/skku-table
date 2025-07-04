package com.skkutable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FestivalCreateDto {

  @NotBlank
  private String name;

  private String description;

  private String location;

  @NotNull
  private Date startDate;

  @NotNull
  private Date endDate;

  private String posterImageUrl;

  private String mapImageUrl;
}
