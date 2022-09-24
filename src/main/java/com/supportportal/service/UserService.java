package com.supportportal.service;

import com.supportportal.domain.User;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName,String username,String email) throws UserNotFoundException, UserNameExistException, EmailExistsException, MessagingException;
    List<User>getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String firstName, String lastName, String username, String email , String role, Boolean isNonLocked , boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException;
    User updateUser(String currentUsername,String newFirstName, String newLastName, String newUsername, String newEmail , String role, Boolean isNonLocked , boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException;
    void deleteUser(long id);
    void resetPassword(String email) throws EmailExistsException, MessagingException;
    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException;



}
