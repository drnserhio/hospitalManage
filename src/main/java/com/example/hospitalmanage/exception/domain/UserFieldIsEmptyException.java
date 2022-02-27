package com.example.hospitalmanage.exception.domain;

public class UserFieldIsEmptyException extends RuntimeException{
    public UserFieldIsEmptyException(String message) {
        super(message);
    }
}
