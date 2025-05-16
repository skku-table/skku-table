package com.skkutable.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name="festival")
public class Festival {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="poster_image_url")
  private String posterImageUrl;

  @Column(name="map_image_url")
  private String mapImageUrl;

  private String name;

  @Column(name="start_date")
  private Date startDate;

  @Column(name="end_date")
  private Date endDate;

  private String location;
  private String description;

  @Column(name="like_count")
  private int likeCount;

  @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<Booth> booths;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition="TIMESTAMP")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition="TIMESTAMP")
  private LocalDateTime UpdatedAt;

  public Festival(String posterImageUrl, String mapImageUrl, String name, Date startDate, Date endDate, String location, String description) {
    this.posterImageUrl = posterImageUrl;
    this.mapImageUrl = mapImageUrl;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.location = location;
    this.description = description;
    this.likeCount = 0;
  }

}
