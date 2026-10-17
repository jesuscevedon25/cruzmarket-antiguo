package com.registro.CruzMarket.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String message;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long userId, String message) {
        this.token = token;
        this.userId = userId;
        this.message = message;
    }

    public LoginResponse(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getClienteId() {
        return this.userId;
    }

}