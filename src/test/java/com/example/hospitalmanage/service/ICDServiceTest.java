package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.ICD;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ICDServiceTest {

    @Autowired
    private ICDService icdService;


    @Test
    @Order(1)
    public void shouldReturnICDCode()
            throws IOException {
        ICD a00 = icdService.getCodeICD("a00");
        assertNotNull(a00);
        assertNotEquals(null, a00);
    }

    @Test
    @Order(2)
    public void shouldReturnListForApi()
            throws IOException {
        String list = icdService.getList();
        assertNotEquals(null, list);
        assertNotNull(list);
    }
}
