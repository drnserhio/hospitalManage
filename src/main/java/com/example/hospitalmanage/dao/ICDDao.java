package com.example.hospitalmanage.dao;

import com.example.hospitalmanage.model.ICD;

import java.io.IOException;

public interface ICDDao {

    ICD getCodeICD(String code) throws IOException;
    ICD findByCode(String code);
    ICD saveICD(ICD icd);
    String getList() throws IOException;
}
