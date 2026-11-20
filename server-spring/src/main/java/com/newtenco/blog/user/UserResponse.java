package com.newtenco.blog.user;

import java.util.List;

public class UserResponse {

    public record DTO(Integer id, String username, String email) {

        public DTO(User user) {
            this(user.getId(), user.getUsername(), user.getEmail());
        }
    }

    public record LoginDTO(String accessToken, String refreshToken, DTO user) {
    }

    public record AvailableDTO(boolean available) {
    }

    public record RolesDTO(Integer id, String username, List<String> roles) {

        public RolesDTO(User user) {
            this(user.getId(), user.getUsername(), user.getRoles());
        }
    }
}
