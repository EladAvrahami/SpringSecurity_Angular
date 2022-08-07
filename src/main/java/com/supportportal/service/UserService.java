package com.supportportal.service;

import com.supportportal.domain.User;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName,String username,String email) throws UserNotFoundException, UserNameExistException, EmailExistsException;
    List<User>getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);



}
