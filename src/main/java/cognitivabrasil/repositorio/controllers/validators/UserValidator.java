/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers.validators;

import cognitivabrasil.repositorio.controllers.UserDto;
import cognitivabrasil.repositorio.data.entities.User;
import cognitivabrasil.repositorio.data.services.UserService;
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

        ValidationUtils.rejectIfEmpty(errors, "nome", "required.nome", "É necessário informar um nome");
        ValidationUtils.rejectIfEmpty(errors, "username", "required.username", "Informar o login");
        ValidationUtils.rejectIfEmpty(errors, "role", "required.role", "Informar o tipo de usuário");


        UserDto u = (UserDto) target;
        
        if (u.getPassword() != null && !u.getPassword().equals(u.getConfirmPass())) {
                errors.rejectValue("confirmPass", "invalid.confirmPass", "As senhas não conferem");
            }
        
        if(!isBlank(u.getPassword()) && u.getPassword().length()<5){
            errors.rejectValue("password", "invalid.password", "Informe uma senha de no mínimo 5 dígitos");
        }
        
        //verifica se ja existe um usuario com o mesmo username
        User uTest = userService.get(u.getUsername());
        if (uTest != null && !uTest.getId().equals(u.getId())) {
            errors.rejectValue("username", "invalid.username", "Já existe um usuário cadastrado com esse login");
        }
    }
}
