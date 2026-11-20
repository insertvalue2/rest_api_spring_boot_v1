package com.newtenco.blog.user;

import com.newtenco.blog.core.util.JwtUtil;
import com.newtenco.blog.core.util.Resp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody UserRequest.JoinDTO reqDTO) {
        return Resp.ok(userService.회원가입(reqDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest.LoginDTO reqDTO) {
        UserResponse.LoginDTO loginDTO = userService.로그인(reqDTO);
        return ResponseEntity.ok()
                .header(JwtUtil.HEADER, loginDTO.accessToken())
                .body(new Resp<>(200, "성공", loginDTO.user()));
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal User sessionUser) {
        return Resp.ok(userService.내정보(sessionUser.getId()));
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        return Resp.ok(userService.유저네임중복체크(username));
    }
}
