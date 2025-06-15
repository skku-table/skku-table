package com.skkutable.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity
@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booth_id", nullable = false)
  private Booth booth;

  @Column(name = "start_time", nullable = false)
  private LocalDateTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalDateTime endTime;

  @Column(name = "max_capacity", nullable = false)
  private Integer maxCapacity;

  @Column(name = "current_capacity", nullable = false)
  @Builder.Default
  private Integer currentCapacity = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private TimeSlotStatus status = TimeSlotStatus.AVAILABLE;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime updatedAt;

  // 비즈니스 메서드
  public void incrementCapacity() {
    if (this.currentCapacity < this.maxCapacity) {
      this.currentCapacity++;
      if (this.currentCapacity >= this.maxCapacity) {
        this.status = TimeSlotStatus.FULL;
      }
    }
  }

  public void decrementCapacity() {
    if (this.currentCapacity > 0) {
      this.currentCapacity--;
      if (this.status == TimeSlotStatus.FULL && this.currentCapacity < this.maxCapacity) {
        this.status = TimeSlotStatus.AVAILABLE;
      }
    }
  }

  public boolean isAvailable() {
    return this.status == TimeSlotStatus.AVAILABLE && this.currentCapacity < this.maxCapacity;
  }

  public boolean canAccommodate(int numberOfPeople) {
    return this.currentCapacity + numberOfPeople <= this.maxCapacity;
  }
}
