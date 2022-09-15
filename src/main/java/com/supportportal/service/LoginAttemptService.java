package com.supportportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    public static final int ATTEMPT_INCREMENT = 5;
    private LoadingCache<String,Integer> loginAttemptCache; //LoadingCache come from google.guava dependency
    //LoadingCache key- the username,value-the num of attempts


    //define con to initialize the cash

    public LoginAttemptService(){
        super();
        loginAttemptCache= CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key){
                        return 0;
                    }
                });
    }


}
