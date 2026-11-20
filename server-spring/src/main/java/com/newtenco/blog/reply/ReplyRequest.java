package com.newtenco.blog.reply;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyRequest {

    public record SaveDTO(
            @NotBlank(message = "댓글을 입력해주세요")
            @Size(max = 100, message = "댓글은 100자 이하로 입력해주세요")
            String comment,

            @NotNull(message = "게시글 번호가 필요합니다")
            Integer boardId) {

        public Reply toEntity(User user, Board board) {
            return Reply.builder()
                    .comment(comment)
                    .user(user)
                    .board(board)
                    .build();
        }
    }
}
