package com.supportportal.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME =432_000_000 ;//5 DAYS expressed in millisecond
     //Bearer-is a way of auth that whoever has this token are verified(they have ownership of him)
     public static final String TOKEN_PREFIX="Bearer ";
    public static final String JWT_TOKEN_HEADER="Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED="Token cannot be verified";
    public static final String GET_ARRAY_LCC="GET ARRAYs LCC";//לדעת מי אישר את הטוקן (גוגל או אמאזון)
    public static final String GET_ARRAY_ADMIN="User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    //                                                       the "**" means that we accept everything after "/user/image/"
    //public static final String[] PUBLIC_URLS = { "/user/login", "/user/register", "/user/image/**" };//urls allow to access without any security
    public static final String[] PUBLIC_URLS = { "**" };//this allow to get access to any url on my program without authenticate


}
