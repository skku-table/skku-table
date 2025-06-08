package com.skkutable.dto;

import com.skkutable.domain.Booth;
import com.skkutable.domain.Festival;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostContentResponseDto {

  private List<FestivalWithBooths> festivals;
  private List<BoothResponse> booths;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FestivalWithBooths {

    private Long id;
    private String name;
    private String posterImageUrl;
    private String mapImageUrl;
    private String startDate;
    private String endDate;
    private String location;
    private String description;
    private Integer likeCount;
    private List<BoothResponse> booths;

    public static FestivalWithBooths from(Festival festival, List<Booth> booths) {
      return FestivalWithBooths.builder()
          .id(festival.getId())
          .name(festival.getName())
          .posterImageUrl(festival.getPosterImageUrl())
          .mapImageUrl(festival.getMapImageUrl())
          .startDate(festival.getStartDate() != null ? festival.getStartDate().toString() : null)
          .endDate(festival.getEndDate() != null ? festival.getEndDate().toString() : null)
          .location(festival.getLocation())
          .description(festival.getDescription())
          .likeCount(festival.getLikeCount())
          .booths(booths.stream()
              .map(BoothResponse::from)
              .toList())
          .build();
    }
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class BoothResponse {

    private Long id;
    private Long festivalId;
    private String festivalName;
    private String name;
    private String host;
    private String location;
    private String description;
    private String startDateTime;
    private String endDateTime;
    private Integer likeCount;
    private String posterImageUrl;
    private String eventImageUrl;

    public static BoothResponse from(Booth booth) {
      return BoothResponse.builder()
          .id(booth.getId())
          .festivalId(booth.getFestival() != null ? booth.getFestival().getId() : null)
          .festivalName(booth.getFestival() != null ? booth.getFestival().getName() : null)
          .name(booth.getName())
          .host(booth.getHost())
          .location(booth.getLocation())
          .description(booth.getDescription())
          .startDateTime(
              booth.getStartDateTime() != null ? booth.getStartDateTime().toString() : null)
          .endDateTime(booth.getEndDateTime() != null ? booth.getEndDateTime().toString() : null)
          .likeCount(booth.getLikeCount())
          .posterImageUrl(booth.getPosterImageUrl())
          .eventImageUrl(booth.getEventImageUrl())
          .build();
    }
  }
}
