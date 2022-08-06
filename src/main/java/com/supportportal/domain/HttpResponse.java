package com.supportportal.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

//every req to my application return something that have this class properties
//https://projectlombok.org/features/Data
@Getter
@Setter
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Asia/Jerusalem")
    private Date timeStamp;
    private int httpStatusCode; // 200, 201, 400, 500
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    // Constructor never used. Can be (and should be) deleted
    public HttpResponse() {}

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }

}
