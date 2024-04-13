package com.netfliz.netfliz.advice;

import com.netfliz.netfliz.exception.BadCredentialException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ForbiddenExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<Object> handleBadRequestException(BadCredentialException ex) {
        BuildResponse builder = new BuildResponse();

        return new ResponseEntity<>(builder.buildResponse(HttpStatus.FORBIDDEN, ex), HttpStatus.FORBIDDEN);
    }
}
