package com.skkutable.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.skkutable.dto.BoothPatchDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "booth")
public class Booth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "festival_id")
  @JsonBackReference
  private Festival festival;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  @JsonBackReference
  private User createdBy;

  private String name;
  private String host;
  private String location;
  private String description;

  @Column(name = "start_date_time")
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time")
  private LocalDateTime endDateTime;

  @Setter
  @Column(name = "like_count")
  @Builder.Default
  private Integer likeCount = 0;

  @Setter
  @Column(name = "poster_image_url")
  private String posterImageUrl;

  @Setter
  @Column(name = "event_image_url")
  private String eventImageUrl;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime UpdatedAt;

  public void setFestival(Festival festival) {
    this.festival = festival;
  }

  public void applyPatch(BoothPatchDto dto, Festival targetFestival) {

    // ① 연관관계(페스티벌) 변경
    if (targetFestival != null) {
      this.festival = targetFestival;
    }

    // ② 일반 필드들
    if (dto.getName() != null) {
      this.name = dto.getName();
    }
    if (dto.getHost() != null) {
      this.host = dto.getHost();
    }
    if (dto.getLocation() != null) {
      this.location = dto.getLocation();
    }
    if (dto.getDescription() != null) {
      this.description = dto.getDescription();
    }
    if (dto.getStartDateTime() != null) {
      this.startDateTime = dto.getStartDateTime();
    }
    if (dto.getEndDateTime() != null) {
      this.endDateTime = dto.getEndDateTime();
    }
    if (dto.getLikeCount() != null) {
      this.likeCount = dto.getLikeCount();
    }
    if (dto.getPosterImageUrl() != null) {
      this.posterImageUrl = dto.getPosterImageUrl();
    }
    if (dto.getEventImageUrl() != null) {
      this.eventImageUrl = dto.getEventImageUrl();
    }
  }

  public void incrementLikeCount() {
    this.likeCount += 1;
  }

  public void decrementLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount -= 1;
    }
  }

  public void setCreatedBy(User user) {
    this.createdBy = user;
  }
}