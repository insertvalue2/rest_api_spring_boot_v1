package com.newtenco.blog.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {

    public record JoinDTO(
            @NotBlank(message = "유저네임을 입력해주세요")
            @Size(min = 4, max = 20, message = "유저네임은 4~20자여야 합니다")
            String username,

            @NotBlank(message = "비밀번호를 입력해주세요")
            @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
            String password,

            @NotBlank(message = "이메일을 입력해주세요")
            @Email(message = "이메일 형식이 올바르지 않습니다")
            String email) {

        public User toEntity(String encPassword) {
            return User.builder()
                    .username(username)
                    .password(encPassword)
                    .email(email)
                    .build();
        }
    }

    public record LoginDTO(
            @NotBlank(message = "유저네임을 입력해주세요")
            String username,

            @NotBlank(message = "비밀번호를 입력해주세요")
            String password) {
    }

    public record UpdateDTO(String email, String password) {
    }
}
