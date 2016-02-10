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

import com.cognitivabrasil.repositorio.web.UserDto;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 *
 * @author Marcos Nunes <marcosn@gmail.com>
 */
@Component
public class UserValidator implements Validator {

    private UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    public UserValidator() {
    }

    @Override
    public boolean supports(Class clazz) {
        return UserDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmpty(errors, "name", "required.name", "É necessário informar um nome");
        ValidationUtils.rejectIfEmpty(errors, "username", "required.username", "Informar o login");
        ValidationUtils.rejectIfEmpty(errors, "role", "required.role", "Informar o tipo de usuário");

        UserDto u = (UserDto) target;

        if (u.getPassword() != null && !u.getPassword().equals(u.getConfirmPass())) {
            errors.rejectValue("confirmPass", "invalid.confirmPass", "As senhas não conferem");
        }

        if (!isBlank(u.getPassword()) && u.getPassword().length() < 5) {
            errors.rejectValue("password", "invalid.password", "Informe uma senha de no mínimo 5 dígitos");
        }

        //verifica se ja existe um usuario com o mesmo username
        User uTest = userService.get(u.getUsername());
        if (uTest != null && !uTest.getId().equals(u.getId())) {
            if (uTest.isDeleted()) {
                errors.rejectValue("username", "invalid.username", "Existe usuário deletado com esse login, se desejar reativar entre na lista de usuários deletados.");
            } else {
                errors.rejectValue("username", "invalid.username", "Já existe um usuário cadastrado com esse login");
            }
        }
    }
}
