package com.newtenco.blog.board;

import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardResponse.PageDTO 글목록(Integer page, String keyword) {
        Pageable pageable = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
        return new BoardResponse.PageDTO(boardRepository.findAllJoinUser(keyword, pageable));
    }

    @Transactional
    public BoardResponse.ListDTO 글쓰기(BoardRequest.SaveDTO reqDTO, User sessionUser) {
        Board savedBoard = boardRepository.save(reqDTO.toEntity(sessionUser));
        return new BoardResponse.ListDTO(savedBoard);
    }

    public BoardResponse.DetailDTO 글상세(Integer id) {
        Board board = boardRepository.findByIdJoinUser(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        return new BoardResponse.DetailDTO(board);
    }
}
