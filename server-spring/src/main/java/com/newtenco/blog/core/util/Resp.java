package com.newtenco.blog.core.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record Resp<T>(Integer status, String msg, T body) {

    public static <B> ResponseEntity<Resp<B>> ok(B body) {
        return ResponseEntity.ok(new Resp<>(200, "성공", body));
    }

    public static ResponseEntity<Resp<Void>> fail(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(new Resp<>(status.value(), msg, null));
    }
}
