package com.example.demo.API.dto;

public class UserResponseLoginDTO {

    private String username;
    private String password;
    private String email;
    private String avatar;
    private Long id;

    public UserResponseLoginDTO() {}


    public UserResponseLoginDTO(String username, String password, String email, String avatar, Long id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar = avatar;
        this.id = id;
    }


    public UserResponseLoginDTO(String username, String email, String avatar, Long id) {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.id = id;
        this.password = null; // não enviar password na resposta
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}