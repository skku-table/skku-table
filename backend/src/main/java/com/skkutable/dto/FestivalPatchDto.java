package com.skkutable.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에 아예 직렬화하지 않음
public class FestivalPatchDto {
  private String name;
  private String description;
  private String location;
  private Date startDate;
  private Date   endDate;
  private String posterImageUrl;
  private String mapImageUrl;

}
