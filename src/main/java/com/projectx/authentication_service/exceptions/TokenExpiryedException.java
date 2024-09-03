package com.projectx.authentication_service.exceptions;

public class TokenExpiryedException extends RuntimeException{
    public TokenExpiryedException(String msg) {
        super(msg);
    }
}
