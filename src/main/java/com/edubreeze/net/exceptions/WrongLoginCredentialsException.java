package com.edubreeze.net.exceptions;

public class WrongLoginCredentialsException extends ApiClientException {

    public WrongLoginCredentialsException(String message)
    {
        super(message);
    }
}
