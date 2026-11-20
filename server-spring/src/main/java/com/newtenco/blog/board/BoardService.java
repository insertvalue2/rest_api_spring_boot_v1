package com.newtenco.blog.board;

import com.newtenco.blog.core.handler.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse.ListDTO> 글목록() {
        return boardRepository.findAllJoinUser().stream()
                .map(BoardResponse.ListDTO::new)
                .toList();
    }

    public BoardResponse.DetailDTO 글상세(Integer id) {
        Board board = boardRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        return new BoardResponse.DetailDTO(board);
    }
}
