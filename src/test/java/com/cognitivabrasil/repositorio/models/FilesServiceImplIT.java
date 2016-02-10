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
package com.cognitivabrasil.repositorio.models;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.services.DocumentServiceImpl;
import com.cognitivabrasil.repositorio.services.FileServiceImpl;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Integration tests of the UsuarioServiceImpl
 *
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Ignore
public class FilesServiceImplIT extends AbstractServiceTest {

    @Autowired DocumentServiceImpl docService;
    @Autowired FileServiceImpl fileService;
    
    /**
     * Tests that delete() works ok.
     * 
     * We need to remove the file from the document also.
     * @throws IOException
     */
    @Test
    @Ignore
    public void delete() throws IOException {
    	//pre-condition
    	Document d = docService.get(1);
    	assertEquals(2, d.getFiles().size());
    	
    	Files f = fileService.get(1);
    	
    	// create a file so the delete does not fail
    	java.io.File temp = new java.io.File("/tmp/1");
    	temp.createNewFile();
    	temp = null;
    	
    	fileService.deleteFile(f);
    	
    	
    	d = docService.get(1);
    	assertEquals(1, d.getFiles().size());
    }

}
