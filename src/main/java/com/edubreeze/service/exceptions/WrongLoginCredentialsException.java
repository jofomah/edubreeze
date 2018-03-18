package com.edubreeze.service.exceptions;

public class WrongLoginCredentialsException extends Exception {

    public WrongLoginCredentialsException(String message)
    {
        super(message);
    }
}
