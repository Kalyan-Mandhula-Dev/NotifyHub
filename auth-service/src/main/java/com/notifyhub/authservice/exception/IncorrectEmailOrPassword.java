package com.notifyhub.authservice.exception;

public class IncorrectEmailOrPassword extends RuntimeException {
    public IncorrectEmailOrPassword(String message) {
        super(message);
    }
}
