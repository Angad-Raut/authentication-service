package com.projectx.authentication_service.exceptions;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String msg) {
        super(msg);
    }
}
