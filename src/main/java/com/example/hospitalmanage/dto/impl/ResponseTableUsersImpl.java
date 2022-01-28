package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.dto.ResponseTable;
import com.example.hospitalmanage.model.Treatment;
import com.example.hospitalmanage.model.User;
import lombok.Data;

import java.util.List;

@Data
public class ResponseTableUsersImpl implements ResponseTable<User> {

    private int allItemsSize;
    private int page;
    private int totalPages;
    private int size;
    private List<User> content;
    private String sort;
    private String columnSort;

    public ResponseTableUsersImpl(RequestTabel request) {
        init(request);
    }

    public ResponseTable init(RequestTabel request) {
        this.setPage(request.getPage());
        this.setSize(request.getSize());
        this.setSort(request.getSort());
        return this;
    }
}
