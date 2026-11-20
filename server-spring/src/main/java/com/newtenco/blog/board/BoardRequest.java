package com.newtenco.blog.board;

import com.newtenco.blog.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardRequest {

    public record SaveDTO(
            @NotBlank(message = "제목을 입력해주세요")
            @Size(max = 100, message = "제목은 100자 이하로 입력해주세요")
            String title,

            @NotBlank(message = "내용을 입력해주세요")
            @Size(max = 1000, message = "내용은 1000자 이하로 입력해주세요")
            String content) {

        public Board toEntity(User user) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    .build();
        }
    }
}
