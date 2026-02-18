package com.example.pro.configuration;

import com.example.pro.dto.response.ErrorMessageDTO;
import com.example.pro.exception.*;
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
        Map.entry(PatientNotFoundException.class, HttpStatus.NOT_FOUND),
        Map.entry(PractitionerNotFoundException.class, HttpStatus.NOT_FOUND),
        Map.entry(AvailabilityNotFoundException.class, HttpStatus.NOT_FOUND),
        Map.entry(TimeRangeInvalidException.class, HttpStatus.BAD_REQUEST),
        Map.entry(AppointmentOverlapExistedException.class, HttpStatus.CONFLICT),
        Map.entry(InvalidStateChangeException.class, HttpStatus.INTERNAL_SERVER_ERROR)
    );

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorMessageDTO> handleDomainException(DomainException exception) {
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
    public ResponseEntity<ErrorMessageDTO> handleValidationException(Exception exception) {
        return buildResponse(exception.getClass().getSimpleName(), exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleOthers(Exception exception) {
        return buildResponse(exception.getClass().getSimpleName(), "Internal Server Error, please contact admin", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessageDTO> buildResponse(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorMessageDTO(code, message));
    }
}
