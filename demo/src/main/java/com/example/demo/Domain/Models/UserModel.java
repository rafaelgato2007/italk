package com.example.demo.Domain.Models;


import com.example.demo.Infra.Entities.UserEntity;

public class UserModel {
    private Long id;
    private String username;
    private String email;
    private String password; // used only for transfers (never returned)
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public UserModel() {}

    public UserModel(Long id, String username, String email, String password) {
        this.id = id; this.username = username; this.email = email; this.password = password;
    }

    // Convenience constructor from entity
    public UserModel(UserEntity e) {
        this.id = e.getId();
        this.username = e.getUsername();
        this.email = e.getEmail();
        // do NOT copy passwordHash for safety
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
