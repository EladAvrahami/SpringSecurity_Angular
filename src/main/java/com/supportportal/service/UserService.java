package com.supportportal.service;

import com.supportportal.domain.User;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName,String username,String email) throws UserNotFoundException, UserNameExistException, EmailExistsException, MessagingException;
    List<User>getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);



}
