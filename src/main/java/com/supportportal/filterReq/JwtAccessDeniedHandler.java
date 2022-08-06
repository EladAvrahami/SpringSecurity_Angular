package com.supportportal.filterReq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supportportal.constant.SecurityConstant;
import com.supportportal.domain.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static com.supportportal.constant.SecurityConstant.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * return unauthorized 401 to the user
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException Exception) throws IOException {
        //HttpResponse httpResponse =new HttpResponse(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED,HttpStatus.UNAUTHORIZED.getReasonPhrase().toUpperCase(Locale.ROOT), SecurityConstant.ACCESS_DENIED_MESSAGE);
        HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(), ACCESS_DENIED_MESSAGE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream=response.getOutputStream();
        ObjectMapper mapper= new ObjectMapper();
        mapper.writeValue(outputStream,httpResponse);
        outputStream.flush();
    }
}
