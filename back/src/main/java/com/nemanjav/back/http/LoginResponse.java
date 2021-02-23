package com.nemanjav.back.http;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private String account;
    private String name;
    private String role;

    public LoginResponse(String token, String account, String name, String role) {
        this.account = account;
        this.name = name;
        this.token = token;
        this.role = role;
    }
}
