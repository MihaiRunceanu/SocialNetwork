package com.example.guiex1.domain.validators;

import com.example.guiex1.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity.getDate() == null){
            throw new ValidationException("Prietenie nu are o data valida");
        }

        if (entity.getId().getLeft() == entity.getId().getRight() || entity.getId().getRight() == null || entity.getId().getLeft() == null){
            throw new ValidationException("Prietenie invalida");
        }
    }
}
