package com.newtenco.blog.board;

import com.newtenco.blog.reply.Reply;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public class BoardResponse {

    public record ListDTO(Integer id, String title, String content, String username, Timestamp createdAt) {

        public ListDTO(Board board) {
            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getUsername(), board.getCreatedAt());
        }
    }

    public record PageDTO(List<ListDTO> boards, int number, int totalPage, boolean isFirst, boolean isLast) {

        public PageDTO(Page<Board> boardPage) {
            this(boardPage.getContent().stream().map(ListDTO::new).toList(),
                    boardPage.getNumber(),
                    boardPage.getTotalPages(),
                    boardPage.isFirst(),
                    boardPage.isLast());
        }
    }

    public record DetailDTO(Integer id, String title, String content, Integer userId, String username, boolean owner,
                            List<ReplyDTO> replies) {

        public DetailDTO(Board board, Integer sessionUserId) {
            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getId(), board.getUser().getUsername(),
                    board.getUser().getId().equals(sessionUserId),
                    board.getReplies().stream().map(reply -> new ReplyDTO(reply, sessionUserId)).toList());
        }

        public record ReplyDTO(Integer id, String comment, Integer userId, String username, boolean owner) {

            public ReplyDTO(Reply reply, Integer sessionUserId) {
                this(reply.getId(), reply.getComment(), reply.getUser().getId(), reply.getUser().getUsername(),
                        reply.getUser().getId().equals(sessionUserId));
            }
        }
    }
}
