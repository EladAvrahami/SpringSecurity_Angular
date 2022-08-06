package com.supportportal.filterReq;
//filtring req implantation -series of checks like: token is valid usrname correct and more...

import com.supportportal.Utility.JWTProvider;
import com.supportportal.constant.SecurityConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//OncePerRequestFilter abstract class means that everything that going to happening into this
// class its going to happen one
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {


    private JWTProvider jwtProvider;

    public JwtAuthorizationFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    @Override//30 Video - https://poalim.udemy.com/course/jwt-springsecurity-angular/learn/lecture/18340640#overview
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       if (request.getMethod().equalsIgnoreCase(SecurityConstant.OPTIONS_HTTP_METHOD)){
           response.setStatus(HttpStatus.OK.value());
       }else {
           String authorizationHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
           if (authorizationHeader==null || !authorizationHeader.startsWith(SecurityConstant.TOKEN_PREFIX)){
              filterChain.doFilter(request,response);//this line let the req continue its flow
              return;// stop the execution of the method here
           }
           String token=authorizationHeader.substring(SecurityConstant.TOKEN_PREFIX.length()); //give me back the token without bearer
           String username=jwtProvider.getSubject(token);
           if (jwtProvider.isTokenValid(username,token) && SecurityContextHolder.getContext().getAuthentication() == null)
           {
               List<GrantedAuthority> authorities =jwtProvider.getAuthorities(token);//לוקח את רשימת ההרשאות מהטוקן
               Authentication authentication = jwtProvider.getAutentication(username,authorities,request);//להכניס את הפרטים לספרינג סקיוריטי
               SecurityContextHolder.getContext().setAuthentication(authentication);//הכנסה בפועל
           }
           else {
               SecurityContextHolder.clearContext();
           }
       }
        filterChain.doFilter(request,response);
    }
}
