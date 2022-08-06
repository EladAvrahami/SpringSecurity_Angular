package com.supportportal.service.Impl;

import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.repo.UserRepo;
import com.supportportal.service.UserService;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;//spring security interface that call method every time it try to check user authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

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
}
