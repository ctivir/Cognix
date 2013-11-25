/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web.validators;


import com.cognitivabrasil.repositorio.services.UserService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 *
 * @author marcos
 */
public class UserNewValidator extends UserValidator {

    

    public UserNewValidator(UserService userService) {
        super(userService);
    }

    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);



        ValidationUtils.rejectIfEmpty(errors, "password", "required.password", "Informar uma senha de no mínimo 5 dígitos");
        ValidationUtils.rejectIfEmpty(errors, "confirmPass", "required.confirmPass", "Confirme a senha");

    }
}