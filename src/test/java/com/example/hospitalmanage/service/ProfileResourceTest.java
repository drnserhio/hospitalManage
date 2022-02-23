package com.example.hospitalmanage.service;


import com.example.hospitalmanage.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.hospitalmanage.constant.ICDConstant.BEARER;
import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static com.example.hospitalmanage.constant.TestConstant.HOST_TEST;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileResourceTest {

    @LocalServerPort
    private String port;

    private static String token;

    @Test
    @Order(1)
    public void putRequestUpdateUserProfileAndResponseCreated() {
        createToken();
//        JSONObject jsonUser = new JSONObject();
//        jsonUser.put("currentUsername", USERNAME_SUMMER);
//        jsonUser.put("firstname", FIRSTNAME_SUMMER);
//        jsonUser.put("lastname", LASTNAME_SUMMER);
//        jsonUser.put("patronomic", PATRONOMIC_SUMMER);
//        jsonUser.put("age", AGE_SUMMER);
//        jsonUser.put("username", "change" + USERNAME_SUMMER );
//        jsonUser.put("email", EMAIL_TEST);
//        jsonUser.put("QRCODE", "changeQRCODE");
//        jsonUser.put("address", "changeAddress");
//        jsonUser.put("infoAboutComplaint", "changeinfoAboutComplaint");
//        jsonUser.put("infoAboutSick", "changeinfoAboutSick");

        Response put = RestAssured
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
                .queryParam("currentUsername", USERNAME_SUMMER)
                .queryParam("firstname", FIRSTNAME_SUMMER)
                .queryParam("lastname", LASTNAME_SUMMER)
                .queryParam("patronomic", PATRONOMIC_SUMMER)
                .queryParam("age", AGE_SUMMER)
                .queryParam("username", "change" + USERNAME_SUMMER )
                .queryParam("email", EMAIL_TEST)
                .queryParam("QRCODE", "changeQRCODE")
                .queryParam("address", "changeAddress")
                .queryParam("infoAboutComplaint", "changeinfoAboutComplaint")
                .queryParam("infoAboutSick", "changeinfoAboutSick")
                .when()
                .put(HOST_TEST + port + "/account/updateProfile");
        User updateUSerProfile = put.thenReturn().as(User.class);
        assertEquals(CREATED.value(), put.getStatusCode());
        assertNotNull(updateUSerProfile);
        assertNotNull(updateUSerProfile.getId());
        assertEquals(updateUSerProfile.getUsername(), "change" + USERNAME_SUMMER);
        assertEquals(updateUSerProfile.getQRCODE(), "changeQRCODE");
        assertEquals(updateUSerProfile.getAddress(), "changeAddress");
        assertEquals(updateUSerProfile.getInfoAboutComplaint(), "changeinfoAboutComplaint");
        assertEquals(updateUSerProfile.getInfoAboutSick(), "changeinfoAboutSick");
    }

    @Test
    @Order(2)
    public void putRequestUpdateUserProfileAndResponseCreatedWithoutAuthorization() {
        Response put = RestAssured
                .given()
                .queryParam("currentUsername", USERNAME_SUMMER)
                .queryParam("firstname", FIRSTNAME_SUMMER)
                .queryParam("lastname", LASTNAME_SUMMER)
                .queryParam("patronomic", PATRONOMIC_SUMMER)
                .queryParam("age", AGE_SUMMER)
                .queryParam("username", "change" + USERNAME_SUMMER)
                .queryParam("email", EMAIL_TEST)
                .queryParam("QRCODE", "changeQRCODE")
                .queryParam("address", "changeAddress")
                .queryParam("infoAboutComplaint", "changeinfoAboutComplaint")
                .queryParam("infoAboutSick", "changeinfoAboutSick")
                .when()
                .put(HOST_TEST + port + "/account/updateProfile");
        assertEquals(FORBIDDEN.value(), put.getStatusCode());
    }

    @Test
    @Order(3)
    public void putRequestUpdateUserProfileWithBadOrEmptyField() {
        createToken();
        Response put = RestAssured
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
                .queryParam("currentUsername", "")
                .queryParam("first", FIRSTNAME_SUMMER)
                .queryParam("lastname", "")
                .queryParam("patronomic", PATRONOMIC_SUMMER)
                .queryParam("age", AGE_SUMMER )
                .queryParam("username", "change" + USERNAME_SUMMER )
                .queryParam("email", EMAIL_TEST)
                .queryParam("QRCODE", "changeQRCODE")
                .queryParam("address", "changeAddress")
                .queryParam("infoAboutComplaint", "changeinfoAboutComplaint")
                .queryParam("infoAboutSick", "changeinfoAboutSick")
                .when()
                .put(HOST_TEST + port + "/account/updateProfile");
        assertEquals(INTERNAL_SERVER_ERROR.value(), put.getStatusCode());
    }

    @Test
    @Order(4)
    public void getRequestAndResponseDocument() {
        createToken();
        Response get = RestAssured
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
                .pathParams("username", USERNAME_RICK)
                .when()
                .get(HOST_TEST + port + "/account/document/{username}");
        assertEquals(PARTIAL_CONTENT.value(), get.getStatusCode());
    }

    @Test
    @Order(4)
    public void getRequestAndResponseDocumentWithoutAuthorization() {
        Response get = RestAssured
                .given()
                .pathParams("username", USERNAME_RICK)
                .when()
                .get(HOST_TEST + port + "/account/document/{username}");
        assertEquals(FORBIDDEN.value(), get.getStatusCode());
    }

    @Test
    @Order(5)
    public void getRequestAndResponseDocumentWithBadOrEmptyField() {
        Response get = RestAssured
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
                .pathParams("username", ContentType.JSON)
                .when()
                .get(HOST_TEST + port + "/account/document/{username}");
        assertEquals(BAD_REQUEST.value(), get.getStatusCode());

    }

    @Test
    @Order(6)
    public void postRequestAndAddNewAnalyzeToUser() {

    }

    @Test
    @Order(6)
    public void postRequestAndAddNewAnalyzeToUserWithoutAuthorization() {

    }

    @Test
    @Order(6)
    public void postRequestAndAddNewAnalyzeToUserWithBadOrEmptyFiled() {

    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndAddNewTreatmentToUser() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndAddNewTreatmentToUserWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndAddNewTreatmentToUserWithBadOrEmptyFiled() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveTreatmentInUser() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveTreatmentInUserWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveTreatmentInUserWithBadOrEmptyFiled() {
//
//    }
//
//
//    @Test
//    @Order(6)
//    public void postRequestAndChangeHospitalizationInUser() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndChangeHospitalizationInUserWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndChangeHospitalizationInUserWithBadOrEmptyFiled() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndResponseTableDiagnosis() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndResponseTableDiagnosisWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void postRequestAndResponseTableDiagnosisWithBadOrEmptyFiled() {
//
//    }
//
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveAnalyze() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveAnalyzeWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(6)
//    public void delRequestAndRemoveAnalyzeWithBadOrEmptyFiled() {
//
//    }

    public void createToken() {
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
