package com.newtenco.blog.auth;

public class AuthResponse {

    public record ReissueDTO(String accessToken, String refreshToken) {
    }
}
