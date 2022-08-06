package com.supportportal.exception.domain;

public class UserNameExistException extends Exception{
    public UserNameExistException(String message) {
        super(message);
    }
}
