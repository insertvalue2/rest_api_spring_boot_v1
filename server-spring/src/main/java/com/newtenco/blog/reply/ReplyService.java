package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public ReplyResponse.DTO 댓글쓰기(ReplyRequest.SaveDTO reqDTO, User sessionUser) {
        Board board = boardRepository.findById(reqDTO.boardId())
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        Reply savedReply = replyRepository.save(reqDTO.toEntity(sessionUser, board));
        return new ReplyResponse.DTO(savedReply);
    }
}
