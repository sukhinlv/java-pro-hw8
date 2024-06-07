package com.example.exception;

public class UsedLimitNotFound extends RuntimeException {

    public UsedLimitNotFound(String message) {
        super(message);
    }
}
