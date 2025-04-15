package com.example.guiex1.domain;

public class Password {
    private Long userId;
    private String password;
    public Password(String password, Long userId) {
        this.password = password;
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }
}
