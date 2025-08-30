package com.example.KaanBrokage.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ApiError> handleBusiness(NotAllowedException ex){
        ApiError err = new ApiError();
        err.status = HttpStatus.BAD_REQUEST.value();
        err.error = "BUSINESS_RULE_VIOLATION";
        err.message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex){
        ApiError err = new ApiError();
        err.status = HttpStatus.BAD_REQUEST.value();
        err.error = "VALIDATION_ERROR";
        err.message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex){
        ApiError err = new ApiError();
        err.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        err.error = "INTERNAL_ERROR";
        err.message = ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}