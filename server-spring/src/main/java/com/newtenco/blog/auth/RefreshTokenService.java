package com.newtenco.blog.auth;

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
}
