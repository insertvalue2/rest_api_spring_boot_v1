package com.newtenco.blog.core.util;

import org.springframework.http.ResponseEntity;

public record Resp<T>(Integer status, String msg, T body) {

    public static <B> ResponseEntity<Resp<B>> ok(B body) {
        return ResponseEntity.ok(new Resp<>(200, "성공", body));
    }
}
