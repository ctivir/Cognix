/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.services;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author marcos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserServiceImplIT extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    UserService userService;
    
    @Autowired UserDetailsService userDetailService;
    
    @Test
    public void testGetUser(){
        User u = userService.get(2);
        
        assertThat(u.getName(), equalTo("Marcos Nunes"));
        assertThat(u.getUsername(), equalTo("marcos"));
        assertThat(u.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
        assertThat(u.getRole(), equalTo("admin"));
        assertThat(u.getRoleNameText(), equalTo("Administrador de documentos"));
        assertThat(u.getPermissions(), hasSize(3));
    }
    
    @Test
    public void testGetUserByUsername(){
        UserDetails u = userDetailService.loadUserByUsername("marcos");
        
        assertThat(u.getUsername(), equalTo("marcos"));
        assertThat(u.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }
    
    @Test (expected=UsernameNotFoundException.class)
    public void testGetUserByUsernameError(){
        userDetailService.loadUserByUsername("marcola");
    }
    
    @Test
    public void testSaveUser(){
        String nome = "Marcos Nunes";
        String login = "nunes";
        
        User u = new User();
        u.setUsername(login);
        u.setName(nome);
        u.setPassword("teste");
        
        userService.save(u);
        
        User u2 = userService.get(login);
        assertThat(u2.getName(), equalTo(nome));
        assertThat(u2.getUsername(), equalTo(login));
        assertThat(u2.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }
    
    @Test
    public void testDeleteUser(){
        int sizeBefore = userService.getAll().size();
        User u = userService.get("marcos");
        userService.delete(u);
        
        assertThat(userService.getAll().size(), equalTo(sizeBefore-1));
    }
    
    @Test
    public void testEditUser(){
        User u = userService.get("marcos");
        int id = u.getId();
        String username = u.getUsername();
        u.setPassword("marcos");
        userService.save(u);
        
        User u2 = userService.get("marcos");
        assertThat(u2.getId(), equalTo(id));
        assertThat(u2.getUsername(), equalTo(username));
        assertThat(u2.getPassword(), equalTo("c5e3539121c4944f2bbe097b425ee774"));
    }

    
    @Test
    public void testAuthenticate(){
        User u = userService.authenticate("marcos", "teste");
        assertNotNull(u);
    }
    
    @Test
    public void testAuthenticateError(){
        
        assertNull(userService.authenticate("marcos", "senhaErrada"));
        
        assertNull(userService.authenticate(null, "teste"));
        
        assertNull(userService.authenticate("usuarioErrado", "teste"));
    }
}

