package com.BankApp.model;

public class Login {
    private String status;
    private String message;
    private String accessToken;

    // Constructor
    public Login(String status, String message ,  String accessToken) {
        this.status = status;
        this.message = message;
        this.accessToken = accessToken;
    }

    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
