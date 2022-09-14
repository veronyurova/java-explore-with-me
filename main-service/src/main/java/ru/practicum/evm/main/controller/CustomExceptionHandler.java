package ru.practicum.evm.main.controller;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    private static final String REASON = "For the requested operation the conditions are not met.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add("Field: " + error.getField() +
                       ". Error: " + error.getDefaultMessage() +
                       ". Value: " + error.getRejectedValue());
        }
        String reason = "Incorrectly made request.";
        String message = "During [" +  e.getBindingResult().getObjectName() +
                         "] validation " + errors.size() + " errors were found";
        String status = String.valueOf(HttpStatus.BAD_REQUEST);
        log.warn("MethodArgumentNotValidException during [{}] validation",
                 e.getBindingResult().getObjectName());
        return new ApiError(errors, reason, message, status, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleEntityNotFoundException(EntityNotFoundException e) {
        String reason = "The required object was not found.";
        String status = String.valueOf(HttpStatus.NOT_FOUND);
        return new ApiError(null, reason, e.getMessage(), status, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleSQLException(SQLException e) {
        String reason = "Integrity constraint has been violated";
        String status = String.valueOf(HttpStatus.CONFLICT);
        return new ApiError(null, reason, e.getMessage(), status, LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiError handleInternalServerError(Exception e) {
        String reason = "Error occurred";
        String status = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ApiError(null, reason, e.getMessage(), status, LocalDateTime.now());
    }
}

@Getter
@AllArgsConstructor
class ApiError {
    private List<String> errors;
    private String reason;
    private String message;
    private String status;
    private LocalDateTime timestamp;
}
