package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.ForbiddenException;
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

    @Transactional
    public void 댓글삭제(Integer id, Integer sessionUserId) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다"));
        if (!reply.getUser().getId().equals(sessionUserId)) {
            throw new ForbiddenException("댓글을 삭제할 권한이 없습니다");
        }
        replyRepository.delete(reply);
    }
}
