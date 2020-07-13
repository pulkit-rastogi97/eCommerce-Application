package com.udacity.eCommerce.controller;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.udacity.eCommerce.exception.*;
import com.udacity.eCommerce.model.response.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ItemNotFoundException.class)
    protected ResponseEntity<Object> handleItemNotFound(ItemNotFoundException e) {
        return buildResponseEntity(getNotFoundResponse(e));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFound( UserNotFoundException e) {
        return buildResponseEntity(getNotFoundResponse(e));
    }

    @ExceptionHandler(UserNotCreatedException.class)
    protected ResponseEntity<Object> handleUserNotCreatedNotFound( UserNotCreatedException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, e.getMessage());
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(UserNameAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserNameAlreadyExists( UserNameAlreadyExistsException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, e.getMessage());
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<Object> handleUsernameNotFound( UsernameNotFoundException e) {
        return buildResponseEntity(getNotFoundResponse(e));
    }

    @ExceptionHandler(PasswordCriteriaFailedException.class)
    protected ResponseEntity<Object> handlePasswordCriteriaFailed( PasswordCriteriaFailedException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, e.getMessage());
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(PasswordConfirmPasswordDifferentException.class)
    protected ResponseEntity<Object> handlePasswordConfirmPasswordDifferent( PasswordConfirmPasswordDifferentException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, e.getMessage());
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    protected ResponseEntity<Object> handleInsufficientQuantity( InsufficientQuantityException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, e.getMessage());
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntime( RuntimeException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, "Resource Not Available");
        return buildResponseEntity(errorResponseDTO);
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccess( DataAccessException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpStatus.CONFLICT, "DB Server Not Available");
        LOG.error("FAILURE [DATABASE FAILURE OCCURRED] ");
        return buildResponseEntity(errorResponseDTO);
    }

    public ErrorResponseDTO getNotFoundResponse(Exception e){
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND, e.getMessage());
    }

    private ResponseEntity<Object> buildResponseEntity(ErrorResponseDTO errorResponseDTO) {
        return new ResponseEntity<>(errorResponseDTO, errorResponseDTO.getStatus());
    }
}
