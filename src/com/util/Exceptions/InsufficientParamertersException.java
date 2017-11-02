package com.util.Exceptions;

/**
 * Created by rajendv3 on 25/06/2017.
 */
public class InsufficientParamertersException extends Exception {

    public InsufficientParamertersException() {
        super("Insufficient parameters. Heres the doc!!");
    }

    public InsufficientParamertersException(String message){
        super("Insufficient parameters. Heres the doc!!"+"\n\n"+message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
