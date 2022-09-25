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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;//spring security interface that call method every time it try to check user authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.supportportal.constant.FileConstant.*;
import static com.supportportal.constant.FileConstant.JPG_EXTENSION;
import static com.supportportal.constant.UserImplConstant.DEFAULT_USER_IMAGE_PATH;
import static com.supportportal.constant.UserImplConstant.NO_USER_FOUND_BY_EMAIL;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

//import static com.supportportal.enumeration.Role.*;

@Service
@Transactional
@Qualifier("UserDetailsService") //explanation about annotation 35 4:00

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
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        user.setEmail(email);
        user.setJoinDate(new Date());//setJoinDate -set time as current time this method being executed
        user.setPassword(encodePassword(password)); //enable encoded for db testing purposes
        //user.setPassword(password);//testing purposes
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);
        System.out.println("new user password: "+password);
        //emailService.sendNewPasswordEmail(firstName,password,email);
        return user;
    }


    @Override
    public User addNewUser(String firstName, String lastName, String username, String email, String role, Boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        validateNewUsernameAndEmail(EMPTY,username,email);
        User user=new User();
            String password=generatePassword();
            user.setPassword(encodePassword(password));
            user.setUserId(generateUserId());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setJoinDate(new Date());
            user.setUserName(username);
            user.setEmail(email);
            user.setIsActive(isActive);
            user.setIsNotLocked(isNonLocked);
            user.setRole(getRoleEnumName(role).name());
            user.setAuthorities(getRoleEnumName(role).getAuthorities());
            user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
            userRepo.save(user);
            saveProfileImage(user,profileImage);
            return user;
    }



    @Override
    public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, Boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        User currentUser= validateNewUsernameAndEmail(currentUsername,newUsername,newEmail);
        currentUser.setFirstName(newFirstName);
        currentUser.setLastName(newLastName);
        currentUser.setJoinDate(new Date());
        currentUser.setUserName(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setIsActive(isActive);
        currentUser.setIsNotLocked(isNonLocked);
        currentUser.setRole(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());//from name you have get the permissions
        userRepo.save(currentUser);
        saveProfileImage(currentUser,profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(long id) {
        userRepo.deleteById(id);

    }

    /**
     * TODO - ENABLE sendNewPasswordEmail after replace to outlook email
     * @param email user email to send new pass
     * @throws EmailExistsException
     * @throws MessagingException
     */
    @Override
    public void resetPassword(String email) throws EmailExistsException, MessagingException {
        User user=userRepo.findUserByEmail(email);
        if (user==null){
            throw new EmailExistsException(NO_USER_FOUND_BY_EMAIL +email);
        }
        String password =generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
        //send email with new pass to user
        //emailService.sendNewPasswordEmail(user.getFirstName(),password,user.getEmail());
        System.out.println(password);

    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        User user =validateNewUsernameAndEmail(username,null,null);
        saveProfileImage(user,profileImage);
        return user;

    }

    //1:40-74
    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage!=null){
            Path userFolder = Paths.get(USER_FOLDER +user.getUserName()).toAbsolutePath().normalize(); //user/home/supportportal/user/getUserName()
            if (!Files.exists(userFolder)){//if path dont exist crate it
                Files.createDirectories(userFolder);
                System.out.println(DIRECTORY_CREATED + userFolder);
            }
            //do duplicate delete to be sure !!
            Files.deleteIfExists(Paths.get(userFolder+user.getUserName()+DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUserName()+DOT+JPG_EXTENSION), REPLACE_EXISTING);//REPLACE_EXISTING -COME from standard copy options it is replacing the existing pics(remove pic with username from folder)
            user.setProfileImageUrl(setProfileImageUrl(user.getUserName()));
            userRepo.save(user);
            System.out.println(FILE_SAVED_IN_FILE_SYSTEM+ profileImage.getOriginalFilename());
        }
    }


    private String setProfileImageUrl(String username){
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username+ FORWARD_SLASH+
                username+DOT+JPG_EXTENSION).toUriString(); //fromCurrentContextPath()-will give the prefix of address according to project location web

    }
    /**
     *
     * @param role
     * @return role for the specific role name
     */
    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString(); //fromCurrentContextPath()-will give the prefix of address according to project location web
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
