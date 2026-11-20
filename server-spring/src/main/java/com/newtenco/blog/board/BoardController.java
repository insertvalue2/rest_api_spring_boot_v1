package com.newtenco.blog.board;

import com.newtenco.blog.core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/api/boards")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                  @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return Resp.ok(boardService.글목록(page, keyword));
    }

    @GetMapping("/api/boards/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Integer id) {
        return Resp.ok(boardService.글상세(id));
    }
}
