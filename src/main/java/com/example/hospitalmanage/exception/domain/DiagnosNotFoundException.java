package com.example.hospitalmanage.exception.domain;

public class DiagnosNotFoundException extends RuntimeException {
    public DiagnosNotFoundException(String message) {
        super(message);
    }
}
