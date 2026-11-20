package com.newtenco.blog.board;

import com.newtenco.blog.core.handler.errors.ForbiddenException;
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

    public BoardResponse.ListDTO 글수정정보(Integer id, Integer sessionUserId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        if (!board.getUser().getId().equals(sessionUserId)) {
            throw new ForbiddenException("게시글에 접근할 권한이 없습니다");
        }
        return new BoardResponse.ListDTO(board);
    }

    @Transactional
    public BoardResponse.ListDTO 글수정(Integer id, BoardRequest.UpdateDTO reqDTO, Integer sessionUserId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        if (!board.getUser().getId().equals(sessionUserId)) {
            throw new ForbiddenException("게시글을 수정할 권한이 없습니다");
        }
        board.update(reqDTO.title(), reqDTO.content());
        return new BoardResponse.ListDTO(board);
    }

    @Transactional
    public void 글삭제(Integer id, Integer sessionUserId) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        if (!board.getUser().getId().equals(sessionUserId)) {
            throw new ForbiddenException("게시글을 삭제할 권한이 없습니다");
        }
        boardRepository.delete(board);
    }

    public BoardResponse.DetailDTO 글상세(Integer id) {
        Board board = boardRepository.findByIdJoinUserAndReplies(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        return new BoardResponse.DetailDTO(board);
    }
}
