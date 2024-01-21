package com.example.uas_mobile.data.model;

public class RegisterResponse {
    private String message;
    private int userId;

    public RegisterResponse(String message, int userId) {
        this.message = message;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
