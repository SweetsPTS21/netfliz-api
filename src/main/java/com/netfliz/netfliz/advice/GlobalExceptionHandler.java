package com.netfliz.netfliz.advice;

import com.netfliz.netfliz.exception.BadCredentialException;
import com.netfliz.netfliz.exception.BadRequestException;
import com.netfliz.netfliz.model.response.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DefaultResponse<Object> handleException(Exception e) {
        return DefaultResponse.fail(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DefaultResponse<Object> handleBadRequestException(BadRequestException e) {
        return DefaultResponse.fail(e.getMessage());
    }

    @ExceptionHandler(BadCredentialException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public DefaultResponse<Object> handleForbiddenException(BadCredentialException e) {
        return DefaultResponse.fail(e.getMessage());
    }
}
