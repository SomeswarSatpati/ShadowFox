package com.shadowfox.library.model;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private String email;
    private LocalDate joinDate;

    public User() {}

    public User(int id, String name, String email, LocalDate joinDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "User [ID=" + id + ", Name='" + name + "', Email='" + email + "', Joined=" + joinDate + "]";
    }
}
