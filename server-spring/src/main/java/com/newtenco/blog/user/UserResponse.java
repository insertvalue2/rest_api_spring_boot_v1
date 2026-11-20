package com.newtenco.blog.user;

public class UserResponse {

    public record DTO(Integer id, String username, String email) {

        public DTO(User user) {
            this(user.getId(), user.getUsername(), user.getEmail());
        }
    }
}
