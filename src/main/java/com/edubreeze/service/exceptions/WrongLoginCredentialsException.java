package com.edubreeze.service.exceptions;

import com.edubreeze.net.exceptions.ApiClientException;

public class WrongLoginCredentialsException extends Exception {

    public WrongLoginCredentialsException(String message)
    {
        super(message);
    }
}
