/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers.validators;


import cognitivabrasil.repositorio.controllers.UserDto;
import cognitivabrasil.repositorio.data.services.UserService;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 *
 * @author marcos
 */
public class UserEditValidator extends UserValidator{
    
    public UserEditValidator(UserService userService) {
        super(userService);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        
        UserDto u = (UserDto) target;
        
        if(!(isBlank(u.getPassword()) && isBlank(u.getConfirmPass()))) {
            ValidationUtils.rejectIfEmpty(errors, "password", "required.password", "Informe uma senha de no mínimo 5 dígitos");
            ValidationUtils.rejectIfEmpty(errors, "confirmPass", "required.confirmPass", "Confirme a senha");
        }
    }
}
