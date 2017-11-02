package com.util.Exceptions;

/**
 * Created by tripatt1 on 12/10/2017.
 */
public class ConcordionNotFoundException extends Exception {

    public ConcordionNotFoundException() {
        super("Concordion is not present in this artifactory link");
    }

    public ConcordionNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }


}
