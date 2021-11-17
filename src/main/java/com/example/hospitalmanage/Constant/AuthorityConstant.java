package com.example.hospitalmanage.Constant;

public class AuthorityConstant {

    public static final String[] USER_AUTHORITIES = {"user: read"};
    public static final String[] DOCTOR_AUTHORITIES = {"user: read", "user:update"};
    public static final String[] SECRETARY_AUTHORITIES = {"user: read", "user:update"};
    public static final String[] ADMIN_AUTHORITIES = {"user: read", "user:update", "user_delete"};
    public static final String[] SUPER_ADMIN_AUTHORITIES = {"user: read", "user:update", "user_delete", "user:create"};


}
