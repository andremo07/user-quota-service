package com.vicarius.quota.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getLastLoginTimeUtc() {
        return lastLoginTimeUtc;
    }

    public void setLastLoginTimeUtc(LocalDateTime lastLoginTimeUtc) {
        this.lastLoginTimeUtc = lastLoginTimeUtc;
    }

    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
