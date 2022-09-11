package com.supportportal.enumeration;

import static com.supportportal.constant.Authority.*;

//defining the roles that i need to give to the user
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){//tack any num of authority
        this.authorities=authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
