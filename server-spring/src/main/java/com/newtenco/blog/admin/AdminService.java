package com.newtenco.blog.admin;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminService {

    private final BoardRepository boardRepository;

    @Transactional
    public void 관리자_게시글삭제(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        boardRepository.delete(board);
    }
}
