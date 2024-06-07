package com.example.exception;

public class LimitExceeded extends RuntimeException {

    public LimitExceeded(String message) {
        super(message);
    }
}
