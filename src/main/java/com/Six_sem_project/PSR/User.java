package com.Six_sem_project.PSR;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private String username;
    private String email;
    private String password;
    private String mode;

    private boolean banned;
    private LocalDate banUntil;
    private LocalDate lastBannedDate;
    private Integer banCount;
    @ElementCollection
    private List<LocalDate> banHistory = new ArrayList<>();

    public List<LocalDate> getBanHistory() {
        return banHistory;
    }

    public void setBanHistory(List<LocalDate> banHistory) {
        this.banHistory = banHistory;
    }

    public LocalDate getLastBannedDate() {
        return lastBannedDate;
    }

    public void setLastBannedDate(LocalDate lastBannedDate) {
        this.lastBannedDate = lastBannedDate;
    }

    public Integer getBanCount() {
        return banCount;
    }

    public void setBanCount(Integer banCount) {
        this.banCount = banCount;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname ){
        this.fullname=fullname;
    }
    public void setEmail(String email ){
        this.email=email;
    }
    public String getMode() {
        return mode;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    public void  setPassword(String password){
        this.password=password;
    }
    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public LocalDate getBanUntil() {
        return banUntil;
    }

    public void setBanUntil(LocalDate banUntil) {
        this.banUntil = banUntil;
    }
}

