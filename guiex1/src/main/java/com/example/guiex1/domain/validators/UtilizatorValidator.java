package com.example.guiex1.domain.validators;

import com.example.guiex1.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {

        if(entity.getFirstName() == null || entity.getLastName() == null)
            throw new ValidationException("Numele este null");
    }
}
