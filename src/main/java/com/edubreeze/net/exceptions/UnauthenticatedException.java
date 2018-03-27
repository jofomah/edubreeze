package com.edubreeze.net.exceptions;

public class UnauthenticatedException extends ApiClientException {

    public UnauthenticatedException(String message)
    {
        super(message);
    }

    public UnauthenticatedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
