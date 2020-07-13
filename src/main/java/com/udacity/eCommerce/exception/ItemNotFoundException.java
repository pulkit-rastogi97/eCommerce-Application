package com.udacity.eCommerce.exception;

public class ItemNotFoundException extends Exception{

    public ItemNotFoundException(String message){
        super(message);
    }
}
