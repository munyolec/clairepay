
package com.clairepay.gateway.error;

import com.clairepay.gateway.dto.PaymentResponse;
import com.clairepay.gateway.filter.ThreadLocalRequest;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status,
                                                                          WebRequest request) {
        String message = ex.getParameterName() + "is required";
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .responseCode(String.valueOf(ApiErrorCode.MISSING_PARAMETER.getCode()))
                .responseDescription(message)
                .requestId(ThreadLocalRequest.getRequestId())
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        String message = ex.getLocalizedMessage();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .responseCode(String.valueOf(ApiErrorCode.UNREADABLE_MESSAGE.getCode()))
                .responseDescription(message)
                .requestId(ThreadLocalRequest.getRequestId())
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        ObjectError object_error = ex.getBindingResult().getAllErrors().get(0);
        String message = object_error.getDefaultMessage();
        int errorCode;
        assert message != null;
        if (message.contains("required")) {
            errorCode = ApiErrorCode.MISSING_PARAMETER.getCode();
        } else {
            errorCode = ApiErrorCode.INVALID_PARAMETER.getCode();
        }
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .responseCode(String.valueOf(errorCode))
                .responseDescription(message)
                .requestId(ThreadLocalRequest.getRequestId())
                .build();
        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<PaymentResponse> handleInvalidParameter(InvalidParameterException e) {

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .responseCode(String.valueOf(ApiErrorCode.INVALID_PARAMETER.getCode()))
                .responseDescription(e.getMessage())
                .requestId(ThreadLocalRequest.getRequestId())
                .build();

        return new ResponseEntity<>(paymentResponse, HttpStatus.BAD_REQUEST);
    }

}