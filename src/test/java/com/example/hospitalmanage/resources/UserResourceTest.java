package com.example.hospitalmanage.resources;

import com.example.hospitalmanage.dto.impl.RequestTableTreatmentImpl;
import com.example.hospitalmanage.dto.impl.RequestTableUsersImpl;
import com.example.hospitalmanage.model.User;
import com.example.hospitalmanage.util.RequestTableHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.example.hospitalmanage.constant.ICDConstant.BEARER;
import static com.example.hospitalmanage.constant.InitialDataConst.*;
import static com.example.hospitalmanage.constant.TestConstant.HOST_TEST;
import static com.example.hospitalmanage.role.Role.ROLE_ADMIN;
import static com.example.hospitalmanage.role.Role.ROLE_USER;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class UserResourceTest {

    @LocalServerPort
    private String port;
    private static String token;

    @Test
    @Order(1)
    public void postRequestRegistrationUserAndResponseOk() throws JSONException {
        JSONObject usr = new JSONObject();
        usr.put("firstname", FIRSTNAME_TEST_SECOND);
        usr.put("lastname", LASTNAME_TEST_SECOND);
        usr.put("username", USERNAME_TEST_SECOND);
        usr.put("email", EMAIL_TEST_SECOND);
        usr.put("password", PASSWORD_TEST_SECOND);

        Response post = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(usr.toString())
                .when()
                .post(HOST_TEST + port + "/user/register");
        User u = post.thenReturn().as(User.class);
        assertEquals(OK.value(), post.getStatusCode());
        assertNotNull(post.getBody());
        assertNotNull(u.getId());
    }

    @Test
    @Order(2)
    public void postRequestUserSignInAndResponseOk()
            throws JSONException {
        JSONObject usr = new JSONObject();
        usr.put("username", USERNAME_TEST_SECOND);
        usr.put("password", PASSWORD_TEST_SECOND);

        Response post = RestAssured
                .given()
                .body(usr.toString()).contentType(ContentType.JSON)
                .when()
                .post(HOST_TEST + port + "/user/login");
        User u = post.thenReturn().as(User.class);
        assertEquals(OK.value(), post.getStatusCode());
        assertNotNull(post.getBody());
        assertNotNull(u.getId());
        assertEquals(ROLE_USER.name(), u.getRole());
        assertEquals(true, u.getActive());
    }

    @Test
    @Order(3)
    public void postRequestUserSignInWithBadlyOrEmptyFieldReruntBadRequest()
            throws JSONException {
        JSONObject usr = new JSONObject();
        usr.put("username", "");
        usr.put("password", "");

        Response post = RestAssured
                .given().contentType(ContentType.JSON)
                .body(usr.toString())
                .post(HOST_TEST + port + "/user/login");
        assertEquals(BAD_REQUEST.value(), post.getStatusCode());
    }

    @Test
    @Order(4)
    public void postRequestRegistrationUserWithBadOrEmptyField()
            throws JSONException {
        JSONObject usr = new JSONObject();
        usr.put("none", FIRSTNAME_TEST_SECOND);
        usr.put("none", LASTNAME_TEST_SECOND);
        usr.put("none", USERNAME_TEST_SECOND);
        usr.put("none", EMAIL_TEST_SECOND);
        usr.put("", PASSWORD_TEST_SECOND);

        Response post = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(usr.toString())
                .post(HOST_TEST + port + "/user/register");
        assertEquals(INTERNAL_SERVER_ERROR.value(), post.getStatusCode());
    }

    @Test
    @Order(5)
    public void putRequestUpdateUserAccessToSystem()
            throws JSONException {
        createToken();
        User userBeforeUpdate = findUser(USERNAME_JERRY);

        Response putUpdateUserAccess = RestAssured
                .given()
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .queryParam("username", USERNAME_JERRY)
                .queryParam("role", ROLE_ADMIN.name())
                .queryParam("isNonLocked", true)
                .put(HOST_TEST + port + "/update");
        User userAfterUpdate = putUpdateUserAccess.andReturn().as(User.class);
        assertNotNull(token);
        assertEquals(ROLE_USER.name(), userBeforeUpdate.getRole());
        assertEquals(ROLE_ADMIN.name(), userAfterUpdate.getRole());
        assertNotEquals(userBeforeUpdate.getRole(), userAfterUpdate.getRole());
        assertEquals(CREATED.value(), putUpdateUserAccess.getStatusCode());
    }

    @Test
    @Order(6)
    public void putRequestUpdateUserAccessToSystemWithoutAuthorization() {
        Response putUpdateUserAccess = RestAssured
                .given()
                .queryParam("username", USERNAME_JERRY)
                .queryParam("role", ROLE_ADMIN.name())
                .queryParam("isNonLocked", true)
                .put(HOST_TEST + port + "/update");
        assertEquals(FORBIDDEN.value(), putUpdateUserAccess.getStatusCode());
    }


    @Test
    @Order(7)
    public void putRequestUpdateUserAccessWithBadOrEmptyField()
            throws JSONException {
        createToken();
        Response putUpdateUserAccess = RestAssured
                .given()
                .headers(
                        AUTHORIZATION,
                        BEARER + token,
                        CONTENT_TYPE,
                        ContentType.JSON,
                        ACCEPT,
                        ContentType.JSON
                )
                .queryParam("username", ContentType.JSON)
                .queryParam("role", "ROLE_NONE")
                .queryParam("isNonLocked", true)
                .put(HOST_TEST + port + "/update");
        assertEquals(BAD_REQUEST.value(), putUpdateUserAccess.getStatusCode());
    }


    @Test
    @Order(8)
    public void getRequestUsernameAndFindHim()
            throws JSONException {
        createToken();
        User user = findUser(USERNAME_BETTY);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(user.getUsername(), USERNAME_BETTY);
        assertEquals(user.getRole(), ROLE_FOR_BETTY.toUpperCase());
        assertEquals(user.getEmail(), EMAIL_BETTY);
    }

    @Test
    @Order(9)
    public void getRequestUsernameAndFindHimWithoutAuthorization() {
        Response response = RestAssured
                .given()
                .pathParams("username", USERNAME_BETTY)
                .when()
                .get(HOST_TEST + port + "/user/find/{username}");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(10)
    public void getRequestUsernameWithBadOrEmptyField()
            throws JSONException {
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
                .pathParams("username", ContentType.JSON)
                .when()
                .get(HOST_TEST + port + "/user/find/{username}");
        assertEquals(BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    @Order(11)
    public void delRequestUsernameAndDeleteHimWithoutAuthorization() {
        User find = findUser(USERNAME_MORTHY);
        Response response = RestAssured
                .given()
                .pathParams("username", "")
                .when()
                .delete(HOST_TEST + port + "/user/delete/{username}");
        assertNotNull(find);
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(12)
    public void delRequestUsernameWithBadOrEmptyField()
            throws JSONException {
        createToken();
        User find = findUser(USERNAME_MORTHY);
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
                .pathParams("username", ContentType.JSON)
                .when()
                .delete(HOST_TEST + port + "/user/delete/{username}");
        assertEquals(BAD_REQUEST.value(), response.getStatusCode());

    }

    @Test
    @Order(13)
    public void delRequestUsernameAndDeleteHim()
            throws JSONException {
        createToken();
        User find = findUser(USERNAME_MORTHY);
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
                .pathParams("username", USERNAME_MORTHY)
                .when()
                .delete(HOST_TEST + port + "/user/delete/{username}");
        User deleteFormDatabase = findUser(USERNAME_MORTHY);
        assertNotNull(find);
        assertNull(deleteFormDatabase);
    }


    //
//    @Test
//    @Order(5)
//    public void putRequestChangePasswordAndResponseCreatedWithoutAuthorization() {
//
//    }
//
//    @Test
//    @Order(5)
//    public void putRequestChangePasswordAndResponseCreated() {
//
//    }
//
//    @Test
//    @Order(5)
//    public void putRequestChangePasswordWithBadOrEmtpyField() {
//
//    }
//
    @Test
    @Order(14)
    public void getRequestAndResponseListUsers()
            throws JSONException {
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
                .get(HOST_TEST + port + "/user/systemusers");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(15)
    public void getRequestAndResponseListUsersWithoutAuthorization() {
        Response response = RestAssured
                .given()
                .when()
                .get(HOST_TEST + port + "/user/systemusers");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(16)
    public void getRequestAndResponseTableUsers()
            throws JSONException {
        createToken();
        RequestTableUsersImpl requestTableUsers = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTableUsers);
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
                .post(HOST_TEST + port + "/user/list-page");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(17)
    public void getRequestAndResponseTableUsersWithoutAuthorization()
            throws JSONException {
        RequestTableUsersImpl requestTableUsers = new RequestTableUsersImpl();
        RequestTableHelper.init(requestTableUsers);
        createToken();
        Response response = RestAssured
                .given()
                .when()
                .body(requestTableUsers)
                .get(HOST_TEST + port + "/user/list-page");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(18)
    public void getRequestAndResponseTableTreatments()
            throws JSONException {
        createToken();
        RequestTableTreatmentImpl requestTableUsers = new RequestTableTreatmentImpl();
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
                .pathParams("userId", find.getId())
                .post(HOST_TEST + port + "/user/treatments/in/user/{userId}");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(19)
    public void getRequestAndResponseTableTreatmentsWithoutAuthorization()
            throws JSONException {
        createToken();
        RequestTableTreatmentImpl requestTableUsers = new RequestTableTreatmentImpl();
        RequestTableHelper.init(requestTableUsers);
        User find = findUser(USERNAME_RICK);
        Response response = RestAssured
                .given()
                .when()
                .body(requestTableUsers)
                .pathParams("userId", find.getId())
                .get(HOST_TEST + port + "/user/treatments/in/user/{userId}");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
    }

    @Test
    @Order(20)
    public void getRequestAndResponseTableVideos()
            throws JSONException {
        createToken();
        RequestTableTreatmentImpl requestTableUsers = new RequestTableTreatmentImpl();
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
                .pathParams("userId", find.getId())
                .post(HOST_TEST + port + "/user/videos/in/user/{userId}");
        assertEquals(OK.value(), response.getStatusCode());
    }

    @Test
    @Order(21)
    public void getRequestAndResponseTableVideosWithoutAuthorization()
            throws JSONException {
        createToken();
        RequestTableTreatmentImpl requestTableUsers = new RequestTableTreatmentImpl();
        RequestTableHelper.init(requestTableUsers);
        User find = findUser(USERNAME_RICK);
        Response response = RestAssured
                .given()
                .when()
                .body(requestTableUsers)
                .pathParams("userId", find.getId())
                .get(HOST_TEST + port + "/user/videos/in/user/{userId}");
        assertEquals(FORBIDDEN.value(), response.getStatusCode());
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

    public void createToken()
            throws JSONException {
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
