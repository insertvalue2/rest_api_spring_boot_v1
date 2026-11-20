package com.newtenco.blog.auth;

import com.newtenco.blog.core.util.JwtUtil;
import com.newtenco.blog.core.util.Resp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Valid @RequestBody AuthRequest.RefreshDTO reqDTO) {
        AuthResponse.ReissueDTO reissueDTO = refreshTokenService.재발급(reqDTO.refreshToken());
        return ResponseEntity.ok()
                .header(JwtUtil.HEADER, reissueDTO.accessToken())
                .header(RefreshTokenService.HEADER, reissueDTO.refreshToken())
                .body(new Resp<>(200, "성공", null));
    }
}
