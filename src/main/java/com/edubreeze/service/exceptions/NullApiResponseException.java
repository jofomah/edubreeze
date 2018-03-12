package com.edubreeze.service.exceptions;

public class NullApiResponseException extends Exception {
    public NullApiResponseException(String message)
    {
        super(message);
    }

    public NullApiResponseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
