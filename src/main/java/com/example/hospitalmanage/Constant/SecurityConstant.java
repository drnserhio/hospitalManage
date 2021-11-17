package com.example.hospitalmanage.Constant;

public class SecurityConstant {
    public static final long  EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_BE_NOT_VERIFIED = "Token cannot be verified";
    public static final String GET_DRN_SERHIO = "DURAN SERHIO";
    public static final String GET_ARRAYS_ADMINISTRATION = "Portal ps.drn_serhio";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You dont' have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    //public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/resetpassword/**", "/user/image/**"};
    public static final String[] PUBLIC_URLS = {"**"};

}
