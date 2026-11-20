package com.newtenco.blog.user;

public class UserResponse {

    public record DTO(Integer id, String username, String email) {

        public DTO(User user) {
            this(user.getId(), user.getUsername(), user.getEmail());
        }
    }

    public record LoginDTO(String accessToken, DTO user) {
    }

    public record AvailableDTO(boolean available) {
    }
}
