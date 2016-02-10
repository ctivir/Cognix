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
package com.cognitivabrasil.repositorio.repositories;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.data.repositories.UserRepository;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.dao.DataIntegrityViolationException;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserRepositoryIT extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    private UserRepository uRep;
    
    @Test
    public void testGetByUsername(){
        User u = uRep.findByUsername("marcos");
        assertThat(u.getId(), equalTo(2));
    }
    
    @Test
    public void testFindByUsername(){
        int allUsers = uRep.findAll().size();
        int activeUsers = uRep.findByDeletedIsFalse().size();
        assertThat(activeUsers, equalTo(allUsers-1));  
    }
    
    /**
     * Testa que a base de dados não permite dois usuários com o mesmo login
     */
    @Test(expected=DataIntegrityViolationException.class)
    public void testSaveSameLogin(){
        User u = new User();
        u.setUsername("marcos");
        u.setPassword("dd");
        u.setName("chaves");
        uRep.save(u);
    }
}
