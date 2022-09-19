package com.supportportal.service.Impl;

import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.enumeration.Role;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;
import com.supportportal.repo.UserRepo;
import com.supportportal.service.EmailService;
import com.supportportal.service.LoginAttemptService;
import com.supportportal.service.UserService;
//import org.slf4j.Logger;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;//spring security interface that call method every time it try to check user authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static com.supportportal.constant.UserImplConstant.DEFAULT_USER_IMAGE_PATH;

//import static com.supportportal.enumeration.Role.*;

@Service
@Transactional
@Qualifier("UserDetailsService")

public class UserServiceImpl implements UserService, UserDetailsService {

    //private Logger LOGGER= LoggerFactory.getLogger(getClass());//getClass or UserServiceImpl.class is the same

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private EmailService emailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findUserByUserName(username);
        if(user==null){
            //LOGGER.error("user not found by username"+username);
            throw new UsernameNotFoundException(username+"not found");
        }else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());//set login date
            user.setLastLoginDate(new Date());
            userRepo.save(user);//after all checks save user to db
            UserPrincipal userPrincipal =new UserPrincipal(user);
            //LOGGER.info("returning found user by username:"+username);
            return userPrincipal;
        }
    }

    /**
     * check and determine if username is lock or not according to num of trying to log in with incorrect pass
     * @param user
     */
    private void validateLoginAttempt(User user) {
        if (user.isNotLocked()){
          if (loginAttemptService.hasExceededMaxAttempts(user.getUserName())) {
              user.setIsNotLocked(false);
          }else {
              user.setIsNotLocked(true);
          }
        }else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUserName());//remove user from cache
        }
    }


    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UserNameExistException, EmailExistsException, MessagingException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);//StringUtils.EMPTY -give me the option to not provide user name because its first time user come to my app
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword=encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        user.setEmail(email);
        user.setJoinDate(new Date());//setJoinDate -set time as current time this method being executed
        user.setPassword(encodePassword(password));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);
        System.out.println("new user password: "+password);
        emailService.sendNewPasswordEmail(firstName,password,email);
        return user;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString(); //fromCurrentContextPath()-will give the prefix of address according to project location web
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);//password 10 letters long combined char+nums
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);//lang3 library
    }

    /**
     * check UserNotFoundException, UserNameExistException, EmailExistsException
     * @param currentUsername
     * @param newUsername
     * @param newEmail
     * @return valid new current user
     * @throws UserNotFoundException
     * @throws UserNameExistException
     * @throws EmailExistsException
     */
    private User validateNewUsernameAndEmail(String currentUsername,String newUsername,String newEmail) throws UserNotFoundException, UserNameExistException, EmailExistsException {
        User userByNewEmail =findUserByEmail(newEmail);//check if username already been used in my DB
        User userByNewUsername =findUserByUsername(newUsername);

        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser=findUserByUsername(currentUsername);
            if (currentUser==null){
                throw new UserNotFoundException("No user found by username"+ currentUsername);
            }
            if(userByNewUsername !=null && currentUser.getId().equals(userByNewUsername.getId())){
                throw new UserNameExistException("Username already exists");
            }

            if(userByNewEmail !=null && currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistsException("email already exists");
            }
            return currentUser;
        }else {
            if (userByNewUsername !=null){ throw new UserNameExistException("Username already exists"); }
            User userByEmail =findUserByEmail(newEmail);
            if(userByNewEmail !=null){
                throw new EmailExistsException("email already exists");
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepo.findUserByUserName(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }
}
