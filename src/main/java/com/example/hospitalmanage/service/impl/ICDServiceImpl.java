package com.example.hospitalmanage.service.impl;


import com.example.hospitalmanage.dao.ICDDao;
import com.example.hospitalmanage.model.ICD;
import com.example.hospitalmanage.service.ICDService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class ICDServiceImpl implements ICDService {

    private final ICDDao icdDao;

    public ICD getCodeICD(String code) throws IOException {
        ICD codeICD = icdDao.getCodeICD(code);
        return codeICD;
    }

    @Override
    public String getList() throws IOException {
        return icdDao.getList();
    }
}
