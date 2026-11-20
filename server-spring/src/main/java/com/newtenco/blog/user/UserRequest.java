package com.newtenco.blog.user;

public class UserRequest {

    public record JoinDTO(String username, String password, String email) {

        public User toEntity(String encPassword) {
            return User.builder()
                    .username(username)
                    .password(encPassword)
                    .email(email)
                    .build();
        }
    }
}
