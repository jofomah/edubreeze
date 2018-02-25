package com.edubreeze.service.exceptions;

public class MissingRequiredCredentialsException extends Exception {

    public MissingRequiredCredentialsException(String message)
    {
        super(message);
    }

    public MissingRequiredCredentialsException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
