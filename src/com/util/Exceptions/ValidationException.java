package com.util.Exceptions;

/**
 * Created by rajendv3 on 3/07/2017.
 */
public class ValidationException extends Exception{
    public ValidationException() {
    }

    public ValidationException(String message){
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
