package com.supportportal.Controllers;

import com.supportportal.Utility.JWTProvider;
import com.supportportal.domain.HttpResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.supportportal.constant.FileConstant.*;
import static com.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/","/user"})//to be more specific use path instead of value
//whitelabel error will override also the basic url "/" ☝
public class UserResource extends ExceptionHandling {

    //######PAY ATTENTION! after restart this app in the console will be security pass
    //this pass will entered in the login web page spring security made
    //in the login page the default username be "user"
    /*pay attention !
    this path "/user/iswork" is not one of the PUBLIC_URLS so we shouldn't have access to him according to the PUBLIC_URLS i defined*/

    /*@GetMapping(value = "/iswork") //http://localhost:8081/user/iswork
    public String showUser() throws UserNotFoundException {
        // return "application works";
        throw new UserNotFoundException("user not found");
    }*/

    private UserService userService;
    private AuthenticationManager authenticationManager;//from spring-security
    private JWTProvider jwtProvider;

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTProvider jwtProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }


  //if user has been locked go to DB and change filed "is_not_lock" value to 1 (before press apply remove the '1' and just write 1 in sql query)
    @PostMapping("/login")
    public ResponseEntity<User> login1(@RequestBody User user) {
       authenticate(user.getUsername(),user.getPassword());
       User loginUser =userService.findUserByUsername(user.getUsername());
       UserPrincipal userPrincipal =new UserPrincipal(loginUser);
        HttpHeaders jwtHeader= getJWTHeader(userPrincipal);
        return new ResponseEntity<>(loginUser,jwtHeader, HttpStatus.OK);
    }

/*    curl --location --request POST 'http://localhost:8081/user/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userName":"tal",
    "password":"VKOeXHhgyo"
}'   */







    @PostMapping("/register") //http://localhost:8081/user/register
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UserNameExistException, EmailExistsException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/add")
    private ResponseEntity<User>addNewUser(@RequestParam("firstName") String firstname,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("username") String userName,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNotLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        User newUser=userService.addNewUser(firstname,lastName,userName,email,role
        ,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @PostMapping("/update")
    private ResponseEntity<User>updateUser(@RequestParam("currentUserName") String currentUserName,
                                           @RequestParam("firstName") String firstname,
                                           @RequestParam("lastName") String lastName,
                                           @RequestParam("userName") String userName,
                                           @RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isActive") String isActive,
                                           @RequestParam("isNonLocked") String isNonLocked,
                                           @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        User updatedUser=userService.updateUser(currentUserName,firstname,lastName,userName,email,role
                ,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/find/{userName}")
    public ResponseEntity<User>getUser(@PathVariable("username") String username){
        User user=userService.findUserByUsername(username);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>>getAllUsers(){
        List<User> users =userService.getUsers();
        return new ResponseEntity<>(users,HttpStatus.OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse>resetPassword(@PathVariable("email") String email) throws MessagingException, EmailExistsException {
        userService.resetPassword(email);
        return response(HttpStatus.OK,"Email with the new password sent to: "+email);
    }

    @DeleteMapping("/delete{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")//check if the user have the 'user:delete' authority(according to constants folder)
    private ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") long id){
        userService.deleteUser(id);
        return response(HttpStatus.NO_CONTENT,"user has been deleted successfully");
    }

    @PostMapping("/updateProfileImage")
    private ResponseEntity<User>updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage")MultipartFile profileImage) throws UserNotFoundException, UserNameExistException, EmailExistsException, IOException {
        User user=userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /* go to specific folder read its file bytes and return it to browser */
    //80 2:00
    //specify what this method produce (image/jpeg in this case)
    //produces ={IMAGE_JPEG_VALUE,IMAGE_PNG_VALUE} write if you wat it to get also another types of files
    @GetMapping(path="/image/{username}/{fileName}",produces =IMAGE_JPEG_VALUE)
    public byte[] getProfileImage (@PathVariable("username") String username,@PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username+FORWARD_SLASH+fileName));//"user.home" + "/supportportal/user/usrFolder/username.jpg"
    }

    /**
     * this method set robot pic from internet api according to unique username read the stream bytes and
     * @param username will be sent throw the url to get unique robot pic
     * @return producing new image
     * @throws IOException
     */
    //get temporary profile pic form the internet
    @GetMapping(path="/image/profile/{username}",produces =IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage (@PathVariable("username") String username) throws IOException {
        URL url =new URL(TEMP_PROFILE_IMAGE_BASE_URL+ username);//data coming as a stream
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();//store it the data coming from the url in bytes
        try (InputStream inputStream=url.openStream()){
            int bytesRead;
            byte[] chunk =new byte[1024];//read that many bytes from the url stream everytime
            while ((bytesRead=inputStream.read(chunk))>0){
              byteArrayOutputStream.write(chunk,0,bytesRead);//read chunk num 0 until the bytesRead length 0-1042 bytes->0-1042 bytes and so on until reading all the stream
            }
        }
        return byteArrayOutputStream.toByteArray();
    }



    /**
     * making a new httpResponse instance
     * @param httpStatus
     * @param message
     * @return new body and with httpStatus +value+reason that this status got+massage
     * and new http status
     */
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase() ),httpStatus);
    }

    private HttpHeaders getJWTHeader(UserPrincipal user) {
        HttpHeaders headers=new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER,jwtProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String userName, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName,password));
    }


}

