/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.models;

import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import cognitivabrasil.repositorio.data.services.DocumentsServiceImpl;
import cognitivabrasil.repositorio.data.services.FilesServiceImpl;
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
 * @author Paulo Schreiner <paulo@jorjao81.com>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Ignore
public class FilesServiceImplIT extends AbstractServiceTest {

    @Autowired DocumentsServiceImpl docService;
    @Autowired FilesServiceImpl fileService;
    
    /**
     * Tests that delete() works ok.
     * 
     * We need to remove the file from the document also.
     * @throws IOException
     */
    @Test
    public void delete() throws IOException {
    	//pre-condition
    	Document d = docService.get(1);
    	assertEquals(2, d.getFiles().size());
    	
    	Files f = fileService.get(1);
    	
    	// create a file so the delete does not fail
    	java.io.File temp = new java.io.File("/tmp/1");
    	temp.createNewFile();
    	temp = null;
    	
    	fileService.delete(f);
    	
    	
    	d = docService.get(1);
    	assertEquals(1, d.getFiles().size());
    }

}
