package com.supportportal.Utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.supportportal.constant.SecurityConstant;
//import static supportportal.constant.SecurityConstant.*;
import com.supportportal.domain.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class JWTProvider {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * generateJwtToken
     * @param userPrincipal
     * @return Token
     */
    public String generateJwtToken(UserPrincipal userPrincipal){
        String[] claims= getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(SecurityConstant.GET_ARRAY_LCC)
                .withAudience(SecurityConstant.GET_ARRAY_ADMIN)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    //if i can verify that the yoken is correct,i have to tell spring security to get me the authentication
    //of the user and then set that into spring
    //GET authentication when Im passing the user information once i verify the token

    //מתודה מקבלת שם משתמש ורשימה של כל ההרשאות של המשתמשים
    //בסופה אומרת לספרינג סקיוריטי להגיד לספרינג שהיוזר הזה זוהה ושאתה יכול לעבד את המידע המגיע ממנו
    public Authentication getAutentication(String userName, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPassAutToken=new
                UsernamePasswordAuthenticationToken(userName,null,authorities);
        userPassAutToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPassAutToken;
    }

    //check if token is valid:by checking userName is not null & token not expires
    public boolean isTokenValid(String username,String token){
        JWTVerifier verifier=getJWTVerifier();
        return StringUtils.isNotEmpty(username) && ! isTokenExpired (verifier,token);
    }

    /**
     * // JWTVerifier class: https://www.javadoc.io/doc/com.auth0/java-jwt/3.2.0/com/auth0/jwt/JWTVerifier.html
     * @param token
     * @return
     */
    public String getSubject(String token){
        JWTVerifier verifier=getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    /* HELPER METHODS👇*/

    /**
     *  help check expression date of token
     * @param verifier
     * @param token
     * @return boolean answer if the token exp date is before today's date
     */
    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expirationDate=verifier.verify(token).getExpiresAt();//that give me the date token expires
        return expirationDate.before(new Date());//return boolean answer true if the token exp date is before today's date
    }


    //method loop userAuthorities and enter them to authorities list
    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities= new ArrayList<>();
        for (GrantedAuthority grantedAuthority : user.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }



    private String[] getClaimsFromToken(String token){
        JWTVerifier verifier= getJWTVerifier();
        return verifier.verify(token).getClaim(SecurityConstant.AUTHORITIES).asArray(String.class);
    }


    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm =Algorithm.HMAC256(secret);//to read the jwt i should use the same algorithm sing that was created in the "generateJwtToken" method and in "isTokenValid" method
            verifier=JWT.require(algorithm).withIssuer(SecurityConstant.GET_ARRAY_LCC).build();
        }catch (JWTVerificationException exception){//exception-WILL SHOW THE MASSAGE FOR ACTUAL EXCEPTION
            throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);//SELF MADE MASSAGE EXCEPTION
        }
        return verifier;
    }

}
