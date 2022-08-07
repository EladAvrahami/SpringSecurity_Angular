package com.supportportal.service.Impl;

import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;
import com.supportportal.repo.UserRepo;
import com.supportportal.service.UserService;
//import org.slf4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;//spring security interface that call method every time it try to check user authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    //private Logger LOGGER= LoggerFactory.getLogger(getClass());//getClass or UserServiceImpl.class is the same

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findUserByUserName(username);
        if(user==null){
            //LOGGER.error("user not found by username"+username);
            throw new UsernameNotFoundException(username+"not found");
        }else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());//set login date
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal =new UserPrincipal(user);
            //LOGGER.info("returning found user by username:"+username);
            return userPrincipal;
        }
    }


    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UserNameExistException, EmailExistsException {
        validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
        return null;
    }

    /**
     * check UserNotFoundException, UserNameExistException, EmailExistsException
     * @param currentUsername
     * @param newUsername
     * @param newEmail
     * @return
     * @throws UserNotFoundException
     * @throws UserNameExistException
     * @throws EmailExistsException
     */
    private User validateNewUsernameAndEmail(String currentUsername,String newUsername,String newEmail) throws UserNotFoundException, UserNameExistException, EmailExistsException {
        if (StringUtils.isNotBlank(currentUsername)){
            User currentUser=findUserByUsername(currentUsername);
            if (currentUser==null){
                throw new UserNotFoundException("No user found by username"+ currentUsername);
            }
            User userByUsername =findUserByUsername(newUsername);//check if username already been used in my DB
            if(userByUsername!=null && currentUser.getId().equals(userByUsername.getId())){
                throw new UserNameExistException("Username already exists");
            }
            User userByEmail =findUserByEmail(newEmail);//check if username already been used in my DB
            if(userByEmail!=null && currentUser.getId().equals(userByEmail.getId())){
                throw new EmailExistsException("Username already exists");
            }
            return currentUser;
        }else {
            User userByUsername =findUserByUsername(newUsername);
            if (userByUsername!=null){ throw new UserNameExistException("Username already exists"); }

            User userByEmail =findUserByEmail(newEmail);
            if(userByEmail!=null){
                throw new EmailExistsException("Username already exists");
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }
}
