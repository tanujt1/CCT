package com.util.Exceptions;

/**
 * Created by rajendv3 on 30/06/2017.
 */
public class InvalidCredentialsException extends Exception {


    public InvalidCredentialsException(String error) {
        super(error);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
