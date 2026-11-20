package com.newtenco.blog.reply;

import com.newtenco.blog.core.util.Resp;
import com.newtenco.blog.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/api/replies")
    public ResponseEntity<?> save(@AuthenticationPrincipal User sessionUser, @Valid @RequestBody ReplyRequest.SaveDTO reqDTO) {
        return Resp.ok(replyService.댓글쓰기(reqDTO, sessionUser));
    }
}
