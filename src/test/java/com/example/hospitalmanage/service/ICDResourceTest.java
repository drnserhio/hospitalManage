package com.example.hospitalmanage.service;

import com.example.hospitalmanage.model.ICD;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ICDResourceTest {

    @LocalServerPort
    private String port;

    @Test
    @Order(1)
    public void getRequestAndReturnAllInfoICD() {

    }

    @Test
    @Order(2)
    public void getRequestAndReturnAllInfoICDWithBadOrEmptyFiled() {

    }

    @Test
    @Order(3)
    public void getRequestAndReturnAllInfoICDWithoutAuthentication() {

    }

    @Test
    @Order(4)
    public void getRequestAndReturnICD() {

    }

    @Test
    @Order(5)
    public void getRequestAndReturnICDWithBadOrEmptyField() {

    }

    @Test
    @Order(6)
    public void getRequestAndReturnICDWithoutAuthentication() {

    }
}
