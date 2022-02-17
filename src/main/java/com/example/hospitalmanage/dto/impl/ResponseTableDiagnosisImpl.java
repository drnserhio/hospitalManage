package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import lombok.Data;

import javax.persistence.Tuple;
import java.util.List;

@Data
public class ResponseTableDiagnosisImpl implements ResponseTable<Tuple> {
    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<Tuple> content;
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
