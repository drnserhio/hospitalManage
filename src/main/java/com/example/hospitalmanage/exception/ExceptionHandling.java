package com.example.hospitalmanage.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.hospitalmanage.model.HttpResponse;
import com.example.hospitalmanage.exception.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.Objects;

import static com.example.hospitalmanage.constant.HandlingExceptionConstant.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PostMapping(ERROR_PATH_HANDLER)
    public ResponseEntity<HttpResponse> notfound404() {
        return createHttpResponse(NOT_FOUND, IS_NOT_MAPPING_THIS_URL);
    }


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountIsDisabled() {
        return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodIsNotAllowed(HttpRequestMethodNotSupportedException e) {
        HttpMethod httpMethod = Objects.requireNonNull(e.getSupportedHttpMethods().iterator().next());
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, httpMethod));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> accountIsLocked() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }
    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<HttpResponse> passwordIsNotValid() {
        return createHttpResponse(UNAUTHORIZED, PASSWORD_IS_NOT_VALID);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentilas() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTILAS);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerError() {
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException() {
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCCESSING_FILE);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException e) {
        return createHttpResponse(UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UserNameExistsException.class)
    public ResponseEntity<HttpResponse> userNameExistsException(UserNameExistsException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserFieldIsEmptyException.class)
    public ResponseEntity<HttpResponse> userHasEmptyFiledException(UserFieldIsEmptyException e) {
        return createHttpResponse(PARTIAL_CONTENT, e.getMessage());
    }


    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                new HttpResponse(
                status.value(),
                status,
                status.getReasonPhrase().toUpperCase(),
                message.toUpperCase()),
                status
        );
    }


}
