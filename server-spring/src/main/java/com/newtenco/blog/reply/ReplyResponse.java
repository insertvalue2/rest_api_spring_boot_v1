package com.newtenco.blog.reply;

public class ReplyResponse {

    public record DTO(Integer id, String comment, Integer boardId, Integer userId, String username) {

        public DTO(Reply reply) {
            this(reply.getId(), reply.getComment(), reply.getBoard().getId(), reply.getUser().getId(), reply.getUser().getUsername());
        }
    }
}
