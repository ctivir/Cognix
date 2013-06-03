/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers.validators;


import cognitivabrasil.repositorio.controllers.UserDto;
import cognitivabrasil.repositorio.data.entities.User;
import cognitivabrasil.repositorio.data.services.UserService;
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
