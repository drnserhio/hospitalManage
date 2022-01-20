package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.model.Treatment;
import lombok.Data;

@Data
public class RequestTableImpl implements RequestTabel<Treatment> {

    private String column;
    private String sort;
    private int page;
    private int size;

}
