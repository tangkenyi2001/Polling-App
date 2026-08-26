package com.mentimeter.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="users", uniqueConstraints={
    @UniqueConstraint(name="uc_user_email", columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    @Column(nullable=true)
    private LocalDateTime lastLogin;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    public User(String email, String passwordHash){
        this.email=email;
        this.passwordHash=passwordHash;
    }

}
