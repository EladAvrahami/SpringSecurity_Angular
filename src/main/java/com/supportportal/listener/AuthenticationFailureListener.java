package com.supportportal.listener;


import com.supportportal.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationFailureListener {

    @Autowired
    private LoginAttemptService loginAttemptService;


    //UserResource -> "authenticate" mast have a string as principle
    @EventListener //Im listening on that event so whenever it occurs i will grab the principal and add username to cache
    public void  onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String){
            String username=(String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
