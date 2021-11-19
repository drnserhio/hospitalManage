package com.example.hospitalmanage.resource;

import com.example.hospitalmanage.domain.User;
import com.example.hospitalmanage.domain.UserPrincipal;
import com.example.hospitalmanage.exception.ExceptionHandling;
import com.example.hospitalmanage.service.UserService;
import com.example.hospitalmanage.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

import static com.example.hospitalmanage.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/", "/user"})
@AllArgsConstructor
public class UserResource extends ExceptionHandling {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user)
            throws MessagingException {
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
            @RequestParam("profileImage") MultipartFile profileImage)
            throws IOException {
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
           @RequestParam("newRole") String newrRole,
           @RequestParam("isNonLocked") String isNonLocked,
           @RequestParam("isActive") String isActive,
           @RequestParam("profileImage") MultipartFile profileImage)
            throws IOException {
        User updateUser = userService.updateUser(
                currentUsername,
                newFirstname,
                newLastname,
                newUsername,
                newEmail,
                newrRole,
                Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive),
                profileImage
        );
        return new ResponseEntity<>(updateUser, OK);
    }



    private void authentificated(String username, String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }
}
