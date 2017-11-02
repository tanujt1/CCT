package com.util.validators;

import com.util.Exceptions.ValidationException;

import java.util.List;

/**
 * Created by rajendv3 on 4/07/2017.
 */
public class RangeValidator implements CSSValidator {

    List<Integer> rangeList;

    public RangeValidator(List<Integer> rangeList) {
        this.rangeList = rangeList;
    }

    @Override
    public void validate() throws ValidationException {

    }
}
