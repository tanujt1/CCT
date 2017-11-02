package com.util.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.util.Exceptions.InvalidJiraIdException;

/**
 * Created by rajendv3 on 27/06/2017.
 */
public class JiraTaskIdValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws InvalidJiraIdException {

        String[] params = value.split("-");

        if(params.length!=2){
            throw new InvalidJiraIdException("Invalid jira id. e.g PTS-1234");
        }

        try{
            Integer.parseInt(params[1]);
        }catch (NumberFormatException e){
            throw new InvalidJiraIdException(String.format("Jira id:%s must be numeric. e.g PTS-1234",params[1]));
        }

    }

}
