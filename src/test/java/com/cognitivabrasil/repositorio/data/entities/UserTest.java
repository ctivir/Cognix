/*
 * *******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************
 *
 */
package com.cognitivabrasil.repositorio.data.entities;

import java.util.Set;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class UserTest {
    
    @Test
    public void testEqual(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");
        
        assertThat(a.equals(b), equalTo(true));
    }
    
    @Test
    public void testEqualError(){
        User a = new User();
        a.setName("Marcos");
                
        String nome = "Marcos";
        assertThat(a.equals(nome), equalTo(false));
        
    }
    
    @Test
    public void testEqualError2(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError3(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setUsername("marcos");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError4(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setRole("admin");
        b.setUsername("marcos");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError5(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError6(){
        User a = new User();
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError7(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        a.setUsername("marcos");
        b.setPassword("nunes");
        
        assertThat(a.equals(b), equalTo(false));
    }
    
    @Test
    public void testEqualError8(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");
        
        User b = new User();
        b.setId(1);
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setName("Marcos");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setPassword("nunes");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setRole("admin");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setUsername("marcos");
        assertThat(a.equals(b), equalTo(false));
        
    }
    
    @Test
    public void testAuthenticateNull(){
        User a = new User();
        assertThat(a.authenticate(null), equalTo(false));
        assertThat(a.authenticate(""), equalTo(false));
        a.setPassword("");
        assertThat(a.authenticate(null), equalTo(false));
    }
    
    @Test
    public void testSpringSecurityEssentials(){
        User a = new User();
        
        assertThat(a.isAccountNonExpired(), equalTo(true));
        assertThat(a.isAccountNonLocked(), equalTo(true));
        assertThat(a.isCredentialsNonExpired(), equalTo(true));
        assertThat(a.isEnabled(), equalTo(true));
        assertThat(a.getAuthorities(), notNullValue());
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) a.getAuthorities();
        assertThat(authorities, hasSize(0));
    }
    
    @Test
    public void testAuthoritiesAuthor(){
        User a = new User();
        a.setRole("author");
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) a.getAuthorities();
        assertThat(authorities, hasSize(1));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.CREATE_DOC)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_DOC)), equalTo(false));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_USER)), equalTo(false));
    }
    
    @Test
    public void testAuthoritiesView(){
        User a = new User();
        a.setRole("view");
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) a.getAuthorities();
        assertThat(authorities, hasSize(1));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.VIEW)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.CREATE_DOC)), equalTo(false));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_DOC)), equalTo(false));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_USER)), equalTo(false));
    }
    
    @Test
    public void testAuthoritiesAdmin(){
        User a = new User();
        a.setRole(User.ROLE_DOC_ADMIN);
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) a.getAuthorities();
        assertThat(authorities, hasSize(3));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.VIEW)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.CREATE_DOC)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_DOC)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_USER)), equalTo(false));
    }
    
    @Test
    public void testAuthoritiesRoot(){
        User a = new User();
        a.setRole(User.ROLE_ROOT);
        Set<GrantedAuthority> authorities = (Set<GrantedAuthority>) a.getAuthorities();
        assertThat(authorities, hasSize(4));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.VIEW)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.CREATE_DOC)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_DOC)), equalTo(true));
        assertThat(authorities.contains(new SimpleGrantedAuthority(User.MANAGE_USER)), equalTo(true));
    }
    
    @Test
    public void testGetRoleName(){
        User a = new User();
        String result = a.getRoleNameText(); //teste de para ver se da nullPointer
        assertThat(result, nullValue());
        
        a.setRole(User.ROLE_ROOT);
        assertThat(a.getRoleNameText(), equalTo("Superusu\u00e1rio"));
        a.setRole(User.ROLE_DOC_ADMIN);
        assertThat(a.getRoleNameText(), equalTo("Administrador de documentos"));
        a.setRole(User.ROLE_AUTHOR);
        assertThat(a.getRoleNameText(), equalTo("Criador de documentos"));
        a.setRole(User.ROLE_VIEW);
        assertThat(a.getRoleNameText(), equalTo("Somente visualizar"));
    }
    
}
