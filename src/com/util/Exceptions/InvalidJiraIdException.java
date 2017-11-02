package com.util.Exceptions;

import com.beust.jcommander.ParameterException;

/**
 * Created by rajendv3 on 27/06/2017.
 */
public class InvalidJiraIdException extends ParameterException {

    public InvalidJiraIdException(String message) {
        super(message);
    }
}
