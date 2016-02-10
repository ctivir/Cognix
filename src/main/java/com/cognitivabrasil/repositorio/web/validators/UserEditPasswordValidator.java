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
import org.springframework.validation.Errors;

/**
 *
 * @author marcos
 */
public class UserEditPasswordValidator extends UserEditValidator{
    private User orgUser;
    
    public UserEditPasswordValidator(User orgUser,UserService userService) {
        super(userService);
        this.orgUser = orgUser;
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        
        UserDto u = (UserDto) target;
          
        if(!orgUser.authenticate(u.getCurrentPass())){
            errors.rejectValue("currentPass", "invalid.currentPass", "Senha incorreta");
        }
    }
}
