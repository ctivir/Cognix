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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
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
    
    private final String FILETEST = "./src/test/resources/files/file.test";

    @Before
    public void setUp() throws FileNotFoundException {
        cdm = new CollectionDepositManagerImpl();
        File f = new File(FILETEST);
        InputStream is = new FileInputStream(f);
        d = new Deposit(null, is, "file.text", "", null, "md5", null, false);
    }

    @Test
    public void testDeposit() throws SwordError, SwordServerException, SwordAuthException  {
        cdm.createNew(FILETEST, d, new AuthCredentials("user", "user", null), null);
    }
    
}
