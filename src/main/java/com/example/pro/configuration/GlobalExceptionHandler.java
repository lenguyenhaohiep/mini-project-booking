package com.example.pro.configuration;

import com.example.pro.dto.response.ErrorMessageDTO;
import com.example.pro.exception.AppointmentOverlapExisted;
import com.example.pro.exception.AvailabilityNotFound;
import com.example.pro.exception.DomainException;
import com.example.pro.exception.TimeRangeInvalid;
import com.example.pro.exception.PatientNotFound;
import com.example.pro.exception.PractitionerNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_TO_HTTP_STATUS = Map.ofEntries(
        Map.entry(PatientNotFound.class, HttpStatus.BAD_REQUEST),
        Map.entry(PractitionerNotFound.class, HttpStatus.BAD_REQUEST),
        Map.entry(AvailabilityNotFound.class, HttpStatus.BAD_REQUEST),
        Map.entry(TimeRangeInvalid.class, HttpStatus.BAD_REQUEST),
        Map.entry(AppointmentOverlapExisted.class, HttpStatus.CONFLICT)
    );

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ErrorMessageDTO> handleDomainException(DomainException exception) {
        var httpStatus = EXCEPTION_TO_HTTP_STATUS.getOrDefault(exception.getClass(), HttpStatus.BAD_REQUEST);
        return buildResponse(exception.getClass().getSimpleName(), exception.getMessage(), httpStatus);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        NoResourceFoundException.class,
        MethodArgumentNotValidException.class,
        MissingRequestHeaderException.class,
        MissingServletRequestParameterException.class
    })
    ResponseEntity<ErrorMessageDTO> handleValidationException(Exception exception) {
        return buildResponse(exception.getClass().getSimpleName(), exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorMessageDTO> handleOthers(Exception exception) {
        return buildResponse(exception.getClass().getSimpleName(), "Internal Server Error, please contact admin", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessageDTO> buildResponse(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorMessageDTO(code, message));
    }
}
