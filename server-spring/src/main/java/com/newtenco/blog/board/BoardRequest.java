package com.newtenco.blog.board;

import com.newtenco.blog.user.User;

public class BoardRequest {

    public record SaveDTO(String title, String content) {

        public Board toEntity(User user) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    .build();
        }
    }
}
