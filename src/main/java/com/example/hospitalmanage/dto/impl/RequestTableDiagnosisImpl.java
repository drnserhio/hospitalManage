package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.impl.projection.AnalizeProjectionDto;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import lombok.Data;

@Data
public class RequestTableDiagnosisImpl implements RequestTabel<AnalizeProjectionDto> {
    private String column;
    private String sort;
    private int page;
    private int size;
}
