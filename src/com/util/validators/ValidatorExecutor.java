package com.util.validators;

import com.util.Exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajendv3 on 3/07/2017.
 */
public class ValidatorExecutor {

    List<CSSValidator> validatorList=new ArrayList<>();

    public ValidatorExecutor(List<CSSValidator> validatorList) {
        this.validatorList.addAll(validatorList);
    }

    public void execute()throws ValidationException{
        for(CSSValidator validator : validatorList){
            validator.validate();
        }
    }
}
