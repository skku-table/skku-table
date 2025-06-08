package com.skkutable.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에 아예 직렬화하지 않음
public class BoothPatchDto {

  private Long festivalId;
  private String name;
  private String host;
  private String location;
  private String description;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Integer likeCount;
  private String posterImageUrl;
  private String eventImageUrl;
}
