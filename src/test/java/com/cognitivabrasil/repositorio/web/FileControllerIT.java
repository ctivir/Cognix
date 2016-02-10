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
package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.util.Message;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class FileControllerIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private FileController fController;
    @Autowired
    private FileService fService;
    private final String FILETEST = "./src/test/resources/files/file.test";


    private void createFile() throws IOException {
        File testFile = new File(FILETEST);

        try (PrintWriter gravador = new PrintWriter(new FileWriter(testFile))) {
            gravador.print("Arquivo criado pelo teste. Pode ser apagado sem problemas.");
        }
    }

    @Test
    public void testGetFileError() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        fController.getFile(99, response);
        assertThat(response.getStatus(), equalTo(410));

        response = new MockHttpServletResponse();
        fController.getFile(1, response);
        assertThat(response.getStatus(), equalTo(410));
    }

    @Test
    public void testGetFile() throws IOException {
        createFile();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Files f = fService.get(1);
        f.setLocation(FILETEST);
        fController.getFile(1, response);
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeader("Content-Disposition"), equalTo("attachment; filename=" + f.getName()));
        
        FileUtils.forceDelete(new java.io.File(FILETEST));

    }

    @Test
    public void testDelete(){        
        int sizeBefore = fService.getAll().size();

        Message m = fController.delete(1);
        assertThat(m.getType(), equalTo(Message.WARN));

        assertThat(fService.getAll(), hasSize(sizeBefore - 1));

    }

    @Test
    public void testDeleteError() {
        int sizeBefore = fService.getAll().size();
        Message m = fController.delete(99);
        assertThat(m.getType(), equalTo(Message.ERROR));

        assertThat(fService.getAll(), hasSize(sizeBefore));

    }

    @Test
    public void testDeleteWhitFile() throws IOException {
        createFile();
        int sizeBefore = fService.getAll().size();
        Files f = fService.get(1);
        f.setLocation(FILETEST);
        File file = new File(FILETEST);
        assertTrue(file.exists()); //testa se o arquivo foi criado
        Message m = fController.delete(1);
        assertThat(m.getType(), equalTo(Message.SUCCESS));

        assertThat(fService.getAll(), hasSize(sizeBefore - 1));

        assertFalse(file.exists()); //testa se o arquivo foi deletado
    }

}
