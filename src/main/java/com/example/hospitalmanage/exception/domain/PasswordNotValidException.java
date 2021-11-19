package com.example.hospitalmanage.exception.domain;

public class PasswordNotValidException extends RuntimeException{
    public PasswordNotValidException(String message) {
        super(message);
    }
}
