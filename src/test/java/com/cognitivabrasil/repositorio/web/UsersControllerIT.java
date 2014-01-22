package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.util.Message;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UsersControllerIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private UsersController usersController;
    @Autowired
    private UserService userService;
    private final ExtendedModelMap model = new ExtendedModelMap();
    @PersistenceContext
    private EntityManager em;

    @Test
    public void testList() throws Exception {
        User loggerUser = new User();
        loggerUser.setName("marcos");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        String result = usersController.list(model, request);
        assertNotNull(result);
        assertEquals(result, "users/list");
        int total = (int) model.get("total");

        assertThat(total, equalTo(4));
    }

    @Test
    public void testAddGET() {
        String result = usersController.save(model);
        assertNotNull(result);
        assertEquals(result, "users/save");
        UserDto u = (UserDto) model.get("userDto");
        assertNotNull(u);
    }

    @Test
    public void testErrorSenhasDiferentes() {
        UserDto u = new UserDto();
        u.setName("marcos");
        u.setRole("root");
        u.setPassword("aaaaa");
        u.setUsername("dfds");
        u.setConfirmPass("b");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.save(u, bindingResult, model, response);
        assertNotNull(result);
        assertThat(result, equalTo("users/save"));
        assertThat(bindingResult.hasErrors(), equalTo(true));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        assertThat(response.getStatus(), equalTo(200));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("As senhas não conferem"));
    }

    @Test
    public void testErrorSalvarUsuarioExistente() {
        UserDto u = new UserDto();
        u.setName("marcos");
        u.setRole("root");
        u.setPassword("aaaaa");
        u.setConfirmPass("aaaaa");
        u.setUsername("marcos");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.save(u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "users/save");
        assertThat(bindingResult.hasErrors(), equalTo(true));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        assertThat(response.getStatus(), equalTo(200));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("Já existe um usuário cadastrado com esse login"));
    }
    
    @Test
    public void testErrorSalvarUsuarioSemSenha() {
        UserDto u = new UserDto();
        u.setName("marcos");
        u.setUsername("marcoss");
        u.setRole("root");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.save(u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "users/save");
        assertThat(bindingResult.hasErrors(), equalTo(true));
        assertThat(bindingResult.getErrorCount(), equalTo(2));
        assertThat(response.getStatus(), equalTo(200));
    }
    
    @Test
    public void testErrorSalvarSenha4Caracteres() {
        UserDto u = new UserDto();
        u.setName("marcos");
        u.setUsername("marcoss");
        u.setRole("root");
        u.setPassword("1234");
        u.setConfirmPass("1234");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.save(u, bindingResult, model, response);
        assertNotNull(result);
        assertThat(result, equalTo("users/save"));
        assertThat(bindingResult.hasErrors(), equalTo(true));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        assertThat(response.getStatus(), equalTo(200));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("Informe uma senha de no mínimo 5 dígitos"));
    }

    @Test
    public void testSave() {
        int size = userService.getAll().size();
        UserDto u = new UserDto();
        u.setName("marcos");
        u.setPassword("aaaaa");
        u.setConfirmPass("aaaaa");
        u.setRole("root");
        u.setUsername("marcola");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.save(u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "ajax");
        assertThat(bindingResult.hasErrors(), equalTo(false));
        assertThat(response.getStatus(), equalTo(201));
        assertThat(userService.getAll(), hasSize(size+1));
    }

    @Test
    public void testEditGET() {
        String result = usersController.edit(2, model);
        assertNotNull(result);
        assertEquals(result, "users/edit");
        UserDto u = (UserDto) model.get("userDto");
        assertNotNull(u);
        assertThat(u.getName(), equalTo("Marcos Nunes"));
    }

    @Test
    public void testEditPOSTErroSenhasDiferentes() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);

        u.setCurrentPass("teste");
        u.setPassword("dfdsdd");
        u.setConfirmPass("b");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "users/edit");
        assertThat(bindingResult.hasErrors(), equalTo(true));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        assertThat(response.getStatus(), equalTo(200));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("As senhas não conferem"));
    }
    
    @Test
    public void testEditPassGet() throws IOException{
        User uOrg = userService.get(2);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(uOrg, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        String result = usersController.editPass(uOrg.getId(), model, response);
        
        assertThat(result, equalTo("users/editPass"));
        UserDto uDto = (UserDto) model.get("userDto");
        assertThat(uDto.getId(), equalTo(uOrg.getId()));
        assertThat(uDto.getName(), equalTo(uOrg.getName()));
        assertThat(uDto.getUsername(), equalTo(uOrg.getUsername()));
        assertThat(uDto.getPassword(), equalTo(uOrg.getPassword()));
        assertThat(uDto.getRole(), equalTo(uOrg.getRole()));       
        
    }
    
    
    @Test
    public void testEditPassGetPermissionError() throws IOException{
        User uOrg = userService.get(1);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(uOrg, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        String result = usersController.editPass(2, model, response);
        
        assertThat(result, equalTo("ajax"));
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
        
    }
    
    /**
     * Testa se usuário sem permissão PERM_MANAGE_USERS pode alterar a 
     * senha de outro usuário.
     */
    @Test
    public void testEditPassAnotherUser() throws IOException{
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setPassword("new");
        u.setConfirmPass("new");
        u.setCurrentPass("old");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(userService.get(1), "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.editPass(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "ajax");
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
    }
    
    /**
     * Testa se usuário sem permissão de managem user alterar sua senha sem 
     * informar a senha atual.
     */
    @Test
    public void testEditPassWithoutCurrentPass() throws IOException{
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setPassword("12345");
        u.setConfirmPass("12345");
        u.setCurrentPass("");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(uOrg, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.editPass(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "users/editPass");
        assertThat(response.getStatus(), equalTo(200));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("Senha incorreta"));
    }
    
    /**
     * Testa alteração de senha de um usuário com permissão de visualização apenas.
     */
    @Test
    public void testEditPassUserViewer() throws IOException{
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setPassword("12345");
        u.setConfirmPass("12345");
        u.setCurrentPass("teste");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(uOrg, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.editPass(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "ajax");
        assertThat(response.getStatus(), equalTo(201));
        em.flush();
        em.clear();

        User uresult = userService.get(2);
        assertThat(uresult.getName(), equalTo("Marcos Nunes"));
        assertThat(uresult.getPassword(), equalTo("827ccb0eea8a706c4c34a16891f84e7b"));
        
    }

    /**
     * Testa se o sistema permite ao manage users alterar a senha sem informar a 
     * senha atual.
     */
    @Test
    public void testEditPOSTErroSemSenha() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setPassword("12345");
        u.setConfirmPass("12345");
        u.setCurrentPass("");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertEquals(result, "ajax");
        assertThat(response.getStatus(), equalTo(201));
        
        em.flush();
        em.clear();

        User uresult = userService.get(2);
        assertThat(uresult.getName(), equalTo("Marcos Nunes"));
        assertThat(uresult.getPassword(), equalTo("827ccb0eea8a706c4c34a16891f84e7b"));
    }

    @Test
    public void testEditErroConfirmacaoErrada() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setPassword("aaaaa");
        u.setConfirmPass("aaaaab");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertThat(result, equalTo("users/edit"));
        assertThat(bindingResult.hasErrors(), equalTo(true));

        assertThat(response.getStatus(), equalTo(200));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("As senhas não conferem"));
    }
    
 
    @Test
    public void testEditarNomeSemTrocarSenha() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);

        u.setName("Cognitiva");
        u.setUsername("marcos");
        u.setCurrentPass("teste");
        u.setPassword("");
        u.setConfirmPass("");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        
        assertNotNull(result);
        assertThat(result, equalTo("ajax"));

        assertThat(bindingResult.hasErrors(), equalTo(false));
        assertThat(response.getStatus(), equalTo(201));

        em.flush();
        em.clear();

        User uresult = userService.get(2);
        assertThat(uresult.getName(), equalTo("Cognitiva"));
        assertThat(uresult.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }
    
    @Test
    public void testAlterarUsernamePara1Existente() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setUsername("admin");
        u.setCurrentPass("teste");
        u.setPassword(null);
        u.setConfirmPass(null);

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "UserDto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        
        assertNotNull(result);
        assertThat(result, equalTo("users/edit"));
        assertThat(bindingResult.hasErrors(), equalTo(true));

        assertThat(response.getStatus(), equalTo(200));
        assertThat(bindingResult.getErrorCount(), equalTo(1));
        ObjectError erro = bindingResult.getAllErrors().get(0);
        assertThat(erro.getDefaultMessage(), equalTo("Já existe um usuário cadastrado com esse login"));
    }

    @Test
    public void testEditSuccess() {
        User uOrg = userService.get(2);
        UserDto u = new UserDto(uOrg);
        u.setName("Cognitiva");
        u.setPassword("12345");
        u.setConfirmPass("12345");
        u.setCurrentPass("teste");

        BindingResult bindingResult = new BeanPropertyBindingResult(u, "User");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String result = usersController.edit(uOrg.getId(), u, bindingResult, model, response);
        assertNotNull(result);
        assertThat(result, equalTo("ajax"));

        assertThat(bindingResult.hasErrors(), equalTo(false));
        assertThat(response.getStatus(), equalTo(201));

        em.flush();
        em.clear();

        User uresult = userService.get(2);
        assertThat(uresult.getName(), equalTo("Cognitiva"));
        assertThat(uresult.getPassword(), equalTo("827ccb0eea8a706c4c34a16891f84e7b"));
    }

    @Test
    public void testDelete() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        int size = userService.getAll().size();

        Message msg = usersController.delete(2, redirectAttributes);
        assertNotNull(msg);

        assertThat(msg.getType(), equalTo(Message.SUCCESS));
        assertThat(userService.getAll().size(), equalTo(size - 1));

    }
    
    @Test
    public void testDeleteError() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        Message msg = usersController.delete(3333, redirectAttributes);
        assertNotNull(msg);

        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo("Erro ao excluir o usuário"));
    }
}
