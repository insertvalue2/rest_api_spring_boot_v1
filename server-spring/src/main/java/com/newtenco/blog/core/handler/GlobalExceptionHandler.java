package com.newtenco.blog.core.handler;

import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.core.util.Resp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> ex404(NotFoundException e) {
        return Resp.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
