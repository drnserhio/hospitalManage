package com.example.hospitalmanage.role;

import static com.example.hospitalmanage.Constant.AuthorityConstant.*;

public enum Role {

    ROLE_USER(USER_AUTHORITIES),
    ROLE_DOCTOR(DOCTOR_AUTHORITIES),
    ROLE_SECRETARY(SECRETARY_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);


    private String[] authorities;

    Role(String[] authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
