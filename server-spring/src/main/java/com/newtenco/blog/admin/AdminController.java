package com.newtenco.blog.admin;

import com.newtenco.blog.core.util.Resp;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    @GetMapping("/api/admin/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal User sessionUser) {
        return Resp.ok("관리자 " + sessionUser.getUsername() + "님 환영합니다");
    }
}
