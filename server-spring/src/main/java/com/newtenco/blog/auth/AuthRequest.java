package com.newtenco.blog.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    public record RefreshDTO(
            @NotBlank(message = "리프레시 토큰이 필요합니다")
            String refreshToken) {
    }
}
