package com.example.hospitalmanage.resources;

import com.example.hospitalmanage.model.ICD;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.hospitalmanage.constant.ICDConstant.BEARER;
import static com.example.hospitalmanage.constant.InitialDataConst.PASSWORD_RICK;
import static com.example.hospitalmanage.constant.InitialDataConst.USERNAME_RICK;
import static com.example.hospitalmanage.constant.TestConstant.HOST_TEST;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class ICDResourceTest {

    @LocalServerPort
    private String port;
    private static String token;

    @Test
    @Order(1)
    public void getRequestAndReturnAllInfoICD() throws JSONException {
        createToken();
        Response response = RestAssured
                .given()
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .when()
                .get(HOST_TEST + port + "/icd/list");
        assertNotNull(response.getBody());
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(2)
    public void getRequestAndReturnAllInfoICDWithoutAuthentication() {
        Response response = RestAssured
                .when()
                .get(HOST_TEST + port + "/icd/list");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(3)
    public void getRequestAndReturnICD() throws JSONException {
        createToken();
        Response response = RestAssured
                .given()
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .that()
                .pathParams("code", "a00")
                .when()
                .get(HOST_TEST + port + "/icd/{code}");
        ICD icdA00 = response.thenReturn().as(ICD.class);
        assertNotNull(response.getBody());
        assertNotNull(icdA00);
        System.out.println(icdA00.getValue());
        assertEquals("Cholera", icdA00.getValue());
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(4)
    public void getRequestAndReturnICDWithBadOrEmptyField() throws JSONException {
        createToken();
        Response response = RestAssured
                .given()
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .that()
                .pathParams("code", "1231231200")
                .when()
                .get(HOST_TEST + port + "/icd/{code}");
        assertEquals(INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
    }

    @Test
    @Order(5)
    public void getRequestAndReturnICDWithoutAuthentication() {
        Response response = RestAssured
                .given()
                .pathParams("code", "a00")
                .when()
                .get(HOST_TEST + port + "/icd/{code}");
      assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    public void createToken() throws JSONException {
        if (token != null
                && token.length() > 0) {
            return;
        }
        JSONObject usr = new JSONObject();
        usr.put("username", USERNAME_RICK);
        usr.put("password", PASSWORD_RICK);

        Response post = RestAssured
                .given().contentType(ContentType.JSON)
                .body(usr.toString())
                .post(HOST_TEST + port + "/user/login");
        token = post.getHeader("Jwt-Token");
    }
}
