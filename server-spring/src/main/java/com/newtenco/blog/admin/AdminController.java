package com.newtenco.blog.admin;

import com.newtenco.blog.core.util.Resp;
import com.newtenco.blog.user.User;
import com.newtenco.blog.user.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/api/admin/hello")
    public ResponseEntity<?> hello(@AuthenticationPrincipal User sessionUser) {
        return Resp.ok("관리자 " + sessionUser.getUsername() + "님 환영합니다");
    }

    @DeleteMapping("/api/admin/boards/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Integer id) {
        adminService.관리자_게시글삭제(id);
        return Resp.ok(null);
    }

    @PutMapping("/api/admin/users/{id}")
    public ResponseEntity<?> updateRoles(@PathVariable("id") Integer id, @Valid @RequestBody UserRequest.RolesDTO reqDTO) {
        return Resp.ok(adminService.관리자_역할수정(id, reqDTO));
    }
}
