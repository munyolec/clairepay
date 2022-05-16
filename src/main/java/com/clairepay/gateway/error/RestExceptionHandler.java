package com.clairepay.gateway.error;

import com.clairepay.gateway.config.ApiErrorCode;
import com.clairepay.gateway.dto.PaymentResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(MissingServletRequestParameterException.class)

    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers){
        String message = ex.getParameterName() + "is required";
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .response_code(String.valueOf(ApiErrorCode.MISSING_PARAMETER.getCode()))
                .response_description(message)
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);

    }
//    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers){
        String message = ex.getLocalizedMessage();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .response_code(String.valueOf(ApiErrorCode.UNREADABLE_MESSAGE.getCode()))
                .response_description(message)
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers){
       ObjectError object_error = ex.getBindingResult().getAllErrors().get(0);
       String message = object_error.getDefaultMessage();
       int errorCode;
       assert message !=null;
       if(message.contains("required")){
           errorCode = ApiErrorCode.MISSING_PARAMETER.getCode();
       }
       else{
           errorCode = ApiErrorCode.INVALID_PARAMETER.getCode();
       }
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .response_code(String.valueOf(errorCode))
                .response_description(message)
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);

    }
}
