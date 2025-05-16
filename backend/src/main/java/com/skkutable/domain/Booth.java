package com.skkutable.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name="booth")
public class Booth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "festival_id")
  @JsonBackReference
  private Festival festival;

  private String name;
  private String host;
  private String location;
  private String description;

  @Column(name = "start_date_time")
  private LocalDateTime startDateTime;

  @Column(name = "end_date_time")
  private LocalDateTime endDateTime;

  @Column(name = "like_count")
  private int likeCount;

  @Column(name = "poster_image_url")
  private String posterImageUrl;

  @Column(name = "event_image_url")
  private String eventImageUrl;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition="TIMESTAMP")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition="TIMESTAMP")
  private LocalDateTime UpdatedAt;

  public Booth(Festival festival, String name, String host, String location, String description, LocalDateTime startDateTime, LocalDateTime endDateTime, String posterImageUrl, String eventImageUrl) {
    this.festival = festival;
    this.name = name;
    this.host = host;
    this.location = location;
    this.description = description;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.likeCount = 0;
    this.posterImageUrl = posterImageUrl;
    this.eventImageUrl = eventImageUrl;
  }
}