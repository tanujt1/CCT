package com.util.validators;

import com.util.Exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajendv3 on 3/07/2017.
 */
public class JobValidator implements CSSValidator {

    private final String regEx="[A-Z0-9-]";
    private String input;

    public JobValidator(String input) {
        this.input = input;
    }

    @Override
    public void validate() throws ValidationException    {

        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(input);

        boolean match = matcher.matches();

        if(!( StringUtils.isNotEmpty(input) || match))
            throw new ValidationException("Invalid bamboo job id");
    }
}
