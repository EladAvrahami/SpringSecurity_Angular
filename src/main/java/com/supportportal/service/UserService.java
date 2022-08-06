package com.supportportal.service;

import com.supportportal.domain.User;

import java.util.List;

public interface UserService {

    User register(String firstName,String username,String email);
    List<User>getUsers();
    User findByUsername(String username);
    User findUserByEmail(String email);



}
