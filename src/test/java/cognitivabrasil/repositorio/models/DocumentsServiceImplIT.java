/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.models;

import cognitivabrasil.repositorio.data.services.DocumentsServiceImpl;
import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import java.util.HashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
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
public class DocumentsServiceImplIT extends AbstractServiceTest {

    @Autowired
    DocumentsServiceImpl instance;
    
   
    
    @Test
    public void testGet() {
        Document d = instance.get(1);

        assertEquals("test1", d.getObaaEntry());
    }

    @Test
    public void testGetByObaaEntry() {
    	Document d = instance.get("test2");
        assertThat(d, notNullValue());
        assertEquals(2, (int)d.getId());

    }

    @Test
    public void testDelete() {
    	Document d = instance.get(1);
        assertThat(d, notNullValue());
        assertEquals(6, instance.getAll().size());


        instance.delete(d);

        assertEquals(5, instance.getAll().size());

    }
    
    @Test
    public void testDeleteByObaaEntry() {
        instance.deleteByObaaEntry("test1");

        assertEquals(5, instance.getAll().size());
    }
    
    @Test
    public void testSave() {
    	Document d = new Document();
    	d.setObaaEntry("jorjao");
    	
    	HashSet<Files> h = new HashSet<Files>();
    	
    	Files f1 = new Files();
    	f1.setLocation("someplace");
    	f1.setName("file.pdf");
    	h.add(f1);
    	f1.setDocument(d);
    	
    	d.setFiles(h);
    	
    	instance.save(d);
    	
    	instance.flush();
    	
    	Document d2 = instance.get("jorjao");
    	assertThat(d2, notNullValue());
    	
    	assertThat(d2.getFiles(), notNullValue());
    	Files f2 = null;
    	for(Files f : d2.getFiles()) {
    		f2 = f;
    	}
    	assertEquals(f2.getLocation(), "someplace");
    	
    }




}
