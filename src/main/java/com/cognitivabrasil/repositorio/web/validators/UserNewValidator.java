/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

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