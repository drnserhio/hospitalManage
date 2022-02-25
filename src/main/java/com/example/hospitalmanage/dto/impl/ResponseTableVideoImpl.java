package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.Video;
import lombok.Data;

import java.util.List;

@Data
public class ResponseTableVideoImpl implements ResponseTable<Video> {

    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<Video> content;
    private String sort;
    private String columnSort;

    public ResponseTableVideoImpl(RequestTabel request) {
        init(request);
    }

    public ResponseTable init(RequestTabel request) {
        this.setPage(request.getPage());
        this.setSize(request.getSize());
        this.setSort(request.getSort());
        return this;
    }
}