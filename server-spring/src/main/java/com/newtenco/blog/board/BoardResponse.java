package com.newtenco.blog.board;

import java.sql.Timestamp;

public class BoardResponse {

    public record ListDTO(Integer id, String title, String content, String username, Timestamp createdAt) {

        public ListDTO(Board board) {
            this(board.getId(), board.getTitle(), board.getContent(), board.getUser().getUsername(), board.getCreatedAt());
        }
    }
}
