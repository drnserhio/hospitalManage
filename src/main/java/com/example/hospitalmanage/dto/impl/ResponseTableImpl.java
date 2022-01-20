package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.model.Treatment;
import lombok.Data;

import java.util.List;

@Data
public class ResponseTableImpl implements com.example.hospitalmanage.dto.ResponseTable<Treatment> {



    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<Treatment> content;
    private String sort;
    private String columnSort;

    public ResponseTableImpl(RequestTabel request) {
        init(request);
    }

    public com.example.hospitalmanage.dto.ResponseTable init(RequestTabel request) {
        this.setPage(request.getPage());
        this.setSize(request.getSize());
        this.setSort(request.getSort());
        return this;
    }
}