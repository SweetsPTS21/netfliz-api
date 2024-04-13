package com.netfliz.netfliz.advice;

import com.netfliz.netfliz.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class NotFoundExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        BuildResponse builder = new BuildResponse();

        return new ResponseEntity<>(builder.buildResponse(HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }
}