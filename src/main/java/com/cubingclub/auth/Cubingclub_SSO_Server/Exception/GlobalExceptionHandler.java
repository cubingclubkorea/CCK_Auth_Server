package com.cubingclub.auth.Cubingclub_SSO_Server.Exception;

import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorResponseEntity;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomizedException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomizedException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), e.getDetailMessage());
    }
}
