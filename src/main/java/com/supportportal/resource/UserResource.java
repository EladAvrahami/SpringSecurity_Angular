package com.supportportal.resource;

import com.supportportal.domain.User;
import com.supportportal.domain.UserPrincipal;
import com.supportportal.exception.ExceptionHandling;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNameExistException;
import com.supportportal.exception.domain.UserNotFoundException;
import com.supportportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/","/user"})//to be more specific use path instead of value
//whitelabel error will override also the basic url "/" ☝
public class UserResource extends ExceptionHandling {

    //######PAY ATTENTION! after restart this app in the console will be security pass
    //this pass will entered in the login web page spring security made
    //in the login page the default username be "user"
    /*pay attention !
    this path "/user/iswork" is not one of the PUBLIC_URLS so we shouldn't have access to him according to the PUBLIC_URLS i defined*/

    @GetMapping(value = "/iswork") //http://localhost:8081/user/iswork
    public String showUser() throws UserNotFoundException {
        // return "application works";
        throw new UserNotFoundException("user not found");
    }

    @Autowired
    private UserService userService;

    /*@PostMapping("/login") //http://localhost:8081/user/register
    public ResponseEntity<User> register(@RequestBody User user) {
       authenticate(user.getUserName(),user.getPassword());
       User loginUser =userService.findUserByUsername(user.getUserName());
       UserPrincipal userPrincipal =new UserPrincipal(loginUser);
        HttpHeaders jwtHeader= getJWTHeader(userPrincipal);
        return new ResponseEntity<>(loginUser,jwtHeader, HttpStatus.OK);
    }

    private void authenticate(String userName, String password) {
    }*/

    @PostMapping("/register") //http://localhost:8081/user/register
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UserNameExistException, EmailExistsException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

}

