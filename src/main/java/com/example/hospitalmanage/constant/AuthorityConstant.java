package com.example.hospitalmanage.constant;

public final class AuthorityConstant {

    private AuthorityConstant() {}

    public static final String[] USER_AUTHORITIES = {"profile:user" , "profile:change-pass", "profile:chat"};
    public static final String[] DOCTOR_AUTHORITIES = {"profile:user", "profile:change-pass", "profile:chat", "patient:all", "operation:all", "icd:all", "document:all"};
    public static final String[] SECRETARY_AUTHORITIES = {"profile:user", "profile:change-pass", "profile:chat", "patient:all", "icd:all", "document:file" };
    public static final String[] ADMIN_AUTHORITIES = {"patient:all", "profile:all"};
    public static final String[] SUPER_ADMIN_AUTHORITIES = {"god:all"};


}
