/*
 * Copyright (c) 2016 Cognitiva Brasil Tecnologias Educacionais
 * http://www.cognitivabrasil.com.br
 *
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */
package com.cognitivabrasil.repositorio.sword;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.UserService;
import com.hp.hpl.jena.iri.IRI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import org.apache.abdera.Abdera;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

/**
 *
 * @author Igor Pires Ferreira <ipferreira@inf.ufrgs.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class DepositTest {
    
    private CollectionDepositManagerImpl cdm;
    
    private Deposit d;
    
    private final String FILETEST = "./src/test/resources/file.test";

    @Before
    public void setUp() throws FileNotFoundException {
        cdm = new CollectionDepositManagerImpl();
        File f = new File(FILETEST);
        InputStream is = new FileInputStream(f);
        Abdera a = new Abdera();
        d = new Deposit(a.newEntry(), is, "file.text", "", null, "md5", null, false);
        d.setFile(f);
        
        //Autenticação
        UserService uService = mock(UserService.class);
        User u = new User();
        u.setRole(User.ROLE_DOC_ADMIN);
        when(uService.authenticate("user", "user")).thenReturn(u);
        ReflectionTestUtils.setField(cdm, "userService", uService);
    }

    @Test
    public void testDeposit() throws SwordError, SwordServerException, SwordAuthException  {
        DepositReceipt dr = cdm.createNew(FILETEST, d, new AuthCredentials("user", "user", null), null);
        File f = new File(cdm.filesPath+"file.test");
        assertTrue(f.delete());
        assertTrue(dr.getEditIRI().toString().matches(".*\\d+"));
    }
    
}
