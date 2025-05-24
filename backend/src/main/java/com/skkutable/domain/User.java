package com.skkutable.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name="user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    /** BCrypt 해시 저장 */
    private String password;

    /** ROLE_USER 하나만 쓰더라도 필드는 두는 편이 이후 확장에 편함 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition="TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition="TIMESTAMP")
    private LocalDateTime UpdatedAt;

    // 생성자에서 createdAt, updatedAt은 제외
    public User(String name, String email, String encodedPw, Role role) {
        this.name = name;
        this.email = email;
        this.password = encodedPw;
        this.role = role;
    }

}
