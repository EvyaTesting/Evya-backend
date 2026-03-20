//package com.ewe.controller.advice;
//
//package com.ewe.beckn.exception;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import com.twilio.exception.InvalidRequestException;
//
//@ControllerAdvice
//public class BecknExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler(value = {InvalidRequestException.class})
//    protected ResponseEntity<Object> handleInvalidRequest(
//            RuntimeException ex, WebRequest request) {
//        return handleExceptionInternal(ex, ex.getMessage(), 
//            new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler(value = {BecknProtocolException.class})
//    protected ResponseEntity<Object> handleBecknProtocolError(
//            RuntimeException ex, WebRequest request) {
//        return handleExceptionInternal(ex, ex.getMessage(),
//            new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//}
