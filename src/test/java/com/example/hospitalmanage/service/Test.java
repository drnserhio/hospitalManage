package com.example.hospitalmanage.service;

import com.example.hospitalmanage.configuration.PersistenceConfig;
import com.example.hospitalmanage.util.ConnectionJpaProp;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test {

    @Autowired
    private ConnectionJpaProp connectionJpaProp;

    @org.junit.jupiter.api.Test
    @Order(1)
    public void test() {

    }
}
