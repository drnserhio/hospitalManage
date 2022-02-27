package com.example.hospitalmanage.exception.domain;

public class TreatmentNotFoundException extends RuntimeException {
    public TreatmentNotFoundException(String message) {
        super(message);
    }
}
