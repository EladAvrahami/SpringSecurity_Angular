package com.supportportal.resource;

import com.supportportal.exception.ExceptionHandling;
import com.supportportal.exception.domain.EmailExistsException;
import com.supportportal.exception.domain.UserNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
