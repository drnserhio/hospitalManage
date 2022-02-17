package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.ICD;

import java.io.IOException;

public interface ICDService {
    ICD getCodeICD(String code) throws IOException;
    String getList() throws IOException;
}
