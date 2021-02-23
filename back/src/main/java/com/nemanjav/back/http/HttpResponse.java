package com.nemanjav.back.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
public class HttpResponse {

    private int httpStatusCode;

    private HttpStatus httpStatus;

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "MM-dd-yyyy hh:mm:ss")
    private Date timeStamp;

    public HttpResponse(int httpStatusCode , HttpStatus httpStatus ,  String message , Date timeStamp){
        this.timeStamp = new Date();
        this.httpStatus = httpStatus;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }
}
