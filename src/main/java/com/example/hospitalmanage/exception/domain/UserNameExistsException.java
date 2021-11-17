package com.example.hospitalmanage.exception.domain;

public class UserNameExistsException extends RuntimeException{
    public UserNameExistsException(String message) {
        super(message);
    }
}
