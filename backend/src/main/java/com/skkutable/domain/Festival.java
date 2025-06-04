package com.skkutable.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.skkutable.dto.FestivalPatchDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name="festival")
public class Festival {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name="poster_image_url")
  private String posterImageUrl;

  @Setter
  @Column(name="map_image_url")
  private String mapImageUrl;

  private String name;

  @Column(name="start_date")
  private Date startDate;

  @Column(name="end_date")
  private Date endDate;

  private String location;
  private String description;

  @Setter
  @Column(name="like_count")
  @Builder.Default
  private Integer likeCount = 0;

  @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  @Builder.Default
  private List<Booth> booths = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition="TIMESTAMP")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition="TIMESTAMP")
  private LocalDateTime UpdatedAt;

  public void addBooth(Booth booth) {
    booths.add(booth);        // 컬렉션 쪽
    booth.setFestival(this);  // FK 쪽
  }

  public void applyPatch(FestivalPatchDto dto) {
    if (dto.getName()            != null) this.name          = dto.getName();
    if (dto.getDescription()     != null) this.description   = dto.getDescription();
    if (dto.getLocation()        != null) this.location      = dto.getLocation();
    if (dto.getStartDate()       != null) this.startDate     = dto.getStartDate();
    if (dto.getEndDate()         != null) this.endDate       = dto.getEndDate();
    if (dto.getLikeCount()       != null) this.likeCount   = dto.getLikeCount();
    if (dto.getPosterImageUrl()  != null) this.posterImageUrl= dto.getPosterImageUrl();
    if (dto.getMapImageUrl()     != null) this.mapImageUrl   = dto.getMapImageUrl();
  }

  public void incrementLikeCount() {
    this.likeCount += 1;
  }

  public void decrementLikeCount() {
    if (this.likeCount > 0) this.likeCount -= 1;
  }
}
