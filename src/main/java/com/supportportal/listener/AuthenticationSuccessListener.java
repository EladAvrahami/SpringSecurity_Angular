package com.supportportal.listener;

import com.supportportal.domain.User;
import com.supportportal.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * remove username key from cache memory in case it authenticate successfully
     * @param event
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal =event.getAuthentication().getPrincipal();
        if (principal instanceof User){//safety check that principal is kind of user
            User user= (User) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUserName());
        }

    }

}
