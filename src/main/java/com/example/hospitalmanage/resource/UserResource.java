package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.domain.HttpResponse;
import com.example.hospitalmanage.domain.User;
import com.example.hospitalmanage.domain.UserPrincipal;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.exception.domain.EmailExistsException;
import com.example.hospitalmanage.exception.domain.PasswordNotValidException;
import com.example.hospitalmanage.exception.domain.UserNameExistsException;
import com.example.hospitalmanage.exception.domain.UserNotFoundException;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.util.JwtTokenProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.example.hospitalmanage.constant.FileConstant.*;
import static com.example.hospitalmanage.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
@AllArgsConstructor
public class UserResource extends ExceptionHandling {

    public static final String USER_DELETE_SUCCESSFULLY = "User delete successfully";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user)
            throws MessagingException, UserNotFoundException, UserNameExistsException, EmailExistsException {
       User newUser = userService.register(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
       );
       return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestBody User user) {
        authentificated(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeaders, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("isNonLocked") String isNonLocked,
            @RequestParam("isActive") String isActive,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException, MessagingException {
        User newUser = userService.addNewUser(
                firstname,
                lastname,
                username,
                email,
                password,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(newUser, OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(
           @RequestParam("currentUsername") String currentUsername,
           @RequestParam("newFirstname") String newFirstname,
           @RequestParam("newLastname") String newLastname,
           @RequestParam("newUsername") String newUsername,
           @RequestParam("newEmail") String newEmail,
           @RequestParam("role") String role,
           @RequestParam("isNonLocked") String isNonLocked,
           @RequestParam("isActive") String isActive,
           @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws IOException, UserNotFoundException, UserNameExistsException, EmailExistsException {
        User updateUser = userService.updateUser(
                currentUsername,
                newFirstname,
                newLastname,
                newUsername,
                newEmail,
                role,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(updateUser, OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(
            @PathVariable("id") long id) {
        userService.deleteUser(id);
        return response(NO_CONTENT, USER_DELETE_SUCCESSFULLY);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User userByUsername = userService.findUserByUsername(username);
        return new ResponseEntity<>(userByUsername, OK);
    }

    @GetMapping(path = "image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(
            @PathVariable("username") String username)
            throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0 , read);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping(path = "image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(
            @PathVariable("username") String username,
            @PathVariable("profileImage") String filename) throws IOException {

        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    ///------->>>> Doctor




    ///------->>>>

    ///----->>>> Secretary
    @PutMapping("/updatetime")
    @PreAuthorize("hasRole('ROLE_SECRETARY')")
    public ResponseEntity<User> updateUserTimeVisitByUsername(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("timeVisit")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss") LocalDateTime timeVisit)
            throws UserNotFoundException {
        User updateUser = userService.updateUserTimeVisitByUsername(currentUsername, timeVisit);
        return new ResponseEntity<>(updateUser, OK);
    }

    ////------>>>>>>>


   ///------->>>> User

    @PutMapping("/changepass")
    @PreAuthorize("hasAnyAuthority('user:change-pass')")
    public ResponseEntity<User> changePassByUsernameAndOldPassword(
            @RequestParam("currentUsername") String currentUsername,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword)
            throws UserNotFoundException, PasswordNotValidException {
        User user = userService.changePassByUsernameAndOldPassword(currentUsername, oldPassword, newPassword);
        return new ResponseEntity<>(user, OK);
    }


    /////---->>>>>

    private void authentificated(String username, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(),
                        httpStatus,
                        httpStatus.getReasonPhrase().toUpperCase(),
                        message.toUpperCase()),
                        httpStatus);
    }

}
