package com.airbnb.shared.dto.user;


import org.joda.time.LocalDateTime;

public class SessionInfo {

    private final String username;
    private LocalDateTime date;

    public SessionInfo(String username, LocalDateTime date) {
        this.username = username;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}