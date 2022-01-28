package com.example.hospitalmanage.dto.impl;

import com.example.hospitalmanage.dto.RequestTabel;
import com.example.hospitalmanage.model.User;
import lombok.Data;

@Data
public class RequestTableUsersImpl implements RequestTabel<User> {
    private String column;
    private String sort;
    private int page;
    private int size;
}
