package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException() {
        super("Username đã tồn tại");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

