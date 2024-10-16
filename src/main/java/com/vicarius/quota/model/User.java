package com.vicarius.quota.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDateTime lastLoginTimeUtc;
    private boolean locked;

    public User() {
    }

    public User(String firstName, String lastName, LocalDateTime lastLoginTimeUtc) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginTimeUtc = lastLoginTimeUtc;
    }

    public User(Long userId, String firstName, String lastName, LocalDateTime lastLoginTimeUtc) {
        this(firstName, lastName, lastLoginTimeUtc);
        this.id = userId;
    }
}
