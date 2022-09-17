package com.supportportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;


//prevent brute force attack service using google dependency
//(59 8:00)
@Service
public class LoginAttemptService {
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String,Integer> loginAttemptCache; //LoadingCache come from google.guava dependency
    //LoadingCache key- the username,value-the num of attempts


    //define con to initialize the cash

    /**
     * expireAfterWrite- the time cache will expire
     * maximumSize the num of entries the cache can support
     */
    public LoginAttemptService(){
        super();
        loginAttemptCache= CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key){
                        return 0;
                    }
                });
    }

    //move user-key and value from cache
    public void evictUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }


    /**
     * wane Authentication Failure
     * @param username
     * @throws ExecutionException
     */
    public void addUserToLoginAttemptCache(String username) {
        int attempts=0;
        try {
            attempts=loginAttemptCache.get(username) + ATTEMPT_INCREMENT  ;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username,attempts);
    }

    /**
     *
     * @param username is the key
     * @return boolean answer if the key(username) has value bigger or equal to MAXIMUM_NUMBER_OF_ATTEMPTS
     * @throws ExecutionException
     */
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}



/*
---    CACHE ----
USER(key)       ATTEMPTS(value)
user1            1+1+1
user2             1
user3             2
 */
