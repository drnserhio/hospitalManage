package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.dto.impl.projection.AnalizeProjectionDto;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.icd.AnalyzeICDDate;
import lombok.Data;

import java.util.List;

@Data
public class ResponseTableDiagnosisImpl implements ResponseTable<AnalizeProjectionDto> {
    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<AnalizeProjectionDto> content;
    private String sort;
    private String columnSort;

    public ResponseTableDiagnosisImpl(RequestTabel request) {
        init(request);
    }

    public ResponseTable init(RequestTabel request) {
        this.setPage(request.getPage());
        this.setSize(request.getSize());
        this.setSort(request.getSort());
        return this;
    }
}