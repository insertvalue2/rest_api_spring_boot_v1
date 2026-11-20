package com.newtenco.blog.auth;

import com.newtenco.blog.core.handler.errors.UnauthorizedException;
import com.newtenco.blog.core.util.JwtUtil;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RefreshTokenService {

    public static final String HEADER = "Refresh-Token";
    public static final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 14;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String 발급(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(new Timestamp(System.currentTimeMillis() + EXPIRATION_TIME))
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional
    public AuthResponse.ReissueDTO 재발급(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenJoinUser(token)
                .orElseThrow(() -> new UnauthorizedException("다시 로그인해주세요"));
        if (refreshToken.isExpired()) {
            throw new UnauthorizedException("다시 로그인해주세요");
        }
        refreshTokenRepository.delete(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = JwtUtil.create(user);
        String newRefreshToken = 발급(user);
        return new AuthResponse.ReissueDTO(newAccessToken, newRefreshToken);
    }
}
