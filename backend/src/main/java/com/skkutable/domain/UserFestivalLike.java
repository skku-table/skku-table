package com.skkutable.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_festival_like")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFestivalLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
