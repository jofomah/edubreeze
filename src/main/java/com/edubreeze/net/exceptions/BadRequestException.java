package com.edubreeze.net.exceptions;

public class BadRequestException extends ApiClientException {

        public BadRequestException(String message)
        {
            super(message);
        }

        public BadRequestException(String message, Throwable cause)
        {
            super(message, cause);
        }
}
