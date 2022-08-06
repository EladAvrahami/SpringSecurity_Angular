package com.supportportal.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {//https://projectlombok.org/# about @Data annotation
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;//the last time user login
    private Date lastLoginDateDisplay;//used to show when last login
    private Date joinDate;
    private String[] roles;//ROLE_USER{read ,edit},ROLE_ADMIN{delete,update,create}
    private String[] authorities;
    private Boolean isActive;
    private Boolean isNotLocked;


    public boolean isNotLocked() {
        return isNotLocked;
    }

    public boolean isActive() {
        return isActive;
    }
}
