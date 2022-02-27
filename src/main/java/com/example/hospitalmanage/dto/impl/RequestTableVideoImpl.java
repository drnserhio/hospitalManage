package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.model.Video;
import lombok.Data;

@Data
public class RequestTableVideoImpl implements RequestTabel<Video> {
    private String column;
    private String sort;
    private int page;
    private int size;
}
