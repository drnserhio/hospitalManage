package com.example.hospitalmanage.resources;


import com.example.hospitalmanage.dto.impl.RequestTableDiagnosisImpl;
import com.example.hospitalmanage.dto.impl.RequestTableTreatmentImpl;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.util.RequestTableHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
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
import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static com.example.hospitalmanage.constant.TestConstant.HOST_TEST;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class ProfileResourceTest {

    @LocalServerPort
    private String port;

    private static String token;

    @Test
    @Order(1)
    public void putRequestUpdateUserProfileAndResponseCreated()
            throws JSONException {
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
    public void putRequestUpdateUserProfileWithBadOrEmptyField()
            throws JSONException {
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
    public void getRequestAndResponseDocument()
            throws JSONException {
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
    public void postRequestAndChangeHospitalizationInUser()
            throws JSONException {
        createToken();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hospitalization", "true");
        User user = findUser(USERNAME_BETTY);
        Response response = RestAssured
                .given()
                .body(jsonObject.toString())
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .that()
                .pathParams("username", USERNAME_BETTY)
                .when()
                .post(HOST_TEST + port + "/account/change/hospitalization/{username}");
        User updateUser = response.andReturn().as(User.class);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(user.getHospiztalization(), null);
        assertEquals(updateUser.getHospiztalization(), true);
        assertNotEquals(user.getHospiztalization(), updateUser.getHospiztalization());
    }

    @Test
    @Order(7)
    public void postRequestAndChangeHospitalizationInUserWithoutAuthorization()
            throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hospitalization", "true");

        Response response = RestAssured
                .given()
                .body(jsonObject.toString())
                .that()
                .pathParams("username", USERNAME_BETTY)
                .when()
                .post(HOST_TEST + port + "/account/change/hospitalization/{username}");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(8)
    public void postRequestAndChangeHospitalizationInUserWithBadOrEmptyFiled()
            throws JSONException {
        createToken();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ad2", "asd2");
        Response response = RestAssured
                .given()
                .body(jsonObject.toString())
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .that()
                .pathParams("username", USERNAME_BETTY)
                .when()
                .post(HOST_TEST + port + "/account/change/hospitalization/{username}");
        assertEquals(INTERNAL_SERVER_ERROR.value(), response.getStatusCode());

    }


    @Test
    @Order(9)
    public void postRequestAndResponseTableDiagnosis()
            throws JSONException {
        createToken();
        RequestTableDiagnosisImpl requestTableUsers = new RequestTableDiagnosisImpl();
        RequestTableHelper.init(requestTableUsers);
        User find = findUser(USERNAME_RICK);
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
                .body(requestTableUsers)
                .pathParams("id", find.getId())
                .post(HOST_TEST + port + "/account/list/analize/user/{id}");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(10)
    public void postRequestAndResponseTableDiagnosisWithoutAuthorization() {
        RequestTableDiagnosisImpl requestTableUsers = new RequestTableDiagnosisImpl();
        RequestTableHelper.init(requestTableUsers);
        User find = findUser(USERNAME_RICK);
        Response response = RestAssured
                .given()
                .body(requestTableUsers)
                .pathParams("id", find.getId())
                .when()
                .post(HOST_TEST + port + "/account/list/analize/user/{id}");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(11)
    public void postRequestAndResponseTableDiagnosisWithBadOrEmptyFiled()
            throws JSONException {
        createToken();
        User find = findUser(USERNAME_RICK);
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
                .body(ContentType.JSON)
                .pathParams("id", find.getId())
                .post(HOST_TEST + port + "/account/list/analize/user/{id}");
        assertEquals(INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
    }

    public User findUser(Object username) {
        User usr = null;
        try {
            usr = RestAssured
                    .given()
                    .headers(
                            AUTHORIZATION,
                            BEARER + token,
                            CONTENT_TYPE,
                            ContentType.JSON,
                            ACCEPT,
                            ContentType.JSON
                    )
                    .pathParams("username", username)
                    .when()
                    .get(HOST_TEST + port + "/user/find/{username}")
                    .andReturn()
                    .as(User.class);
        } catch (Exception e) {
        }
        return usr;
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
