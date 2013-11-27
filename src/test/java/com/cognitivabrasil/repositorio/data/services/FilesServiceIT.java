/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class FilesServiceIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    FileService fileService;
    @Autowired
    DocumentService docService;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void testSave() {
        int filesBefore = fileService.getAll().size();
        Files f = new Files();
        f.setName("teste.avi");
        f.setLocation("/marcos/nunes");
        f.setContentType("video/x-msvideo");
        f.setSizeInBytes(47446056L);
        fileService.save(f);
        int id = f.getId();
        f = null;

        assertThat(fileService.getAll(), hasSize(filesBefore + 1));

        em.flush();
        em.clear();

        Files f2 = fileService.get(id);
        assertThat(f2, notNullValue());

        assertThat(f2.getName(), equalTo("teste.avi"));
        assertThat(f2.getLocation(), equalTo("/marcos/nunes"));
        assertThat(f2.getContentType(), equalTo("video/x-msvideo"));
        assertThat(f2.getSizeInBytes(), equalTo(47446056L));
        assertThat(f2.getSizeFormatted(), equalTo("45,2 MB"));
    }

    @Test
    public void testGet() {
        Files f = fileService.get(1);

        assertThat(f, notNullValue());
        assertThat(f.getName(), equalTo("teste.txt"));
        assertThat(f.getContentType(), equalTo("text/plain"));
        assertThat(f.getSizeInBytes(), equalTo(42L));
    }

    @Test
    public void testDelete() {
        int filesBefore = fileService.getAll().size();
        int docsBefore = docService.getAll().size();

        Files f = fileService.get(1);
        try {
            fileService.deleteFile(f);
        } catch (IOException io) {
            System.out.println("erro esperado");
        } finally {
            
            
            em.flush();
            em.clear();

            assertThat("Tem que ter deletado um objeto.", fileService.getAll(), hasSize(filesBefore - 1));
            assertThat("Nao pode ter alterado o numero de documentos.", docService.getAll(), hasSize(docsBefore));
            assertThat("O documento tem que ter um arquivo a menos.", docService.get(1).getFiles(), hasSize(1));
        }
    }
}
