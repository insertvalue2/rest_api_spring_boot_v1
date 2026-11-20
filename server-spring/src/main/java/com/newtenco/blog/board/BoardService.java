package com.newtenco.blog.board;

import com.newtenco.blog.core.handler.errors.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse.ListDTO> 글목록(Integer page) {
        Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
        return boardRepository.findAllJoinUser(pageable).getContent().stream()
                .map(BoardResponse.ListDTO::new)
                .toList();
    }

    public BoardResponse.DetailDTO 글상세(Integer id) {
        Board board = boardRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        return new BoardResponse.DetailDTO(board);
    }
}
