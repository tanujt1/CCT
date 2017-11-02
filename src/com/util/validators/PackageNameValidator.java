package com.util.validators;

import com.util.Exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rajendv3 on 4/07/2017.
 */
public class PackageNameValidator implements CSSValidator {

    String regEx = "^[a-zA-Z0-9/]";
    String input;

    public PackageNameValidator(String regEx, String input) {
        this.regEx = regEx;
        this.input = input;
    }

    public PackageNameValidator(String input) {
        this.input = input;
    }

    @Override
    public void validate() throws ValidationException {

        Matcher matcher = Pattern.compile(regEx).matcher(input);

        if(!( StringUtils.isNotEmpty(input) || matcher.matches()))
            throw new ValidationException("Invalid package specified");

    }
}
