package com.udacity.eCommerce.exception;

public class PasswordConfirmPasswordDifferentException extends Exception{

    public PasswordConfirmPasswordDifferentException(String message){
        super(message);
    }
}
