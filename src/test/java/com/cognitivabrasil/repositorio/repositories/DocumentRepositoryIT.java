/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.repositories;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.data.repositories.DocumentRepository;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.joda.time.DateTime;
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
public class DocumentRepositoryIT extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    private DocumentRepository docRep;

    @Test
    public void testGetAll(){
        assertThat(docRep.findAll(), hasSize(5));
    }
    
    @Test
    public void testGetAllToBeDelete() {
        DateTime d = DateTime.now();
        
        List<Document> docs = docRep.findByCreatedLessThanAndActiveIsFalse(d.minusHours(3));
        
        assertThat(docs.size(), equalTo(1));
    }
    
    @Test
    public void testGetAllNotDeleted(){
        List<Document> docs = docRep.findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc();
        assertThat(docs, notNullValue());
        assertThat(docs.size(), equalTo(2));
        
        assertThat(docs.get(0).getId(), equalTo(5));
        assertThat(docs.get(1).getId(), equalTo(1));
    }
    
    @Test
    public void testGetByObaaEntry(){
        Document d = docRep.findByObaaEntry("entry2");
        assertThat(d.getId(), equalTo(2));
    }
    
    @Test
    public void testGetBySubject(){
        Subject s = new Subject();
        s.setId(1);
        s.setName("ciencias");
        List<Document> d = docRep.findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(s);
        assertThat(d.get(0).getId(), equalTo(5));
    }
    
    @Test
    public void testGetByOwner(){
        User u = new User();
        u.setId(2);
        long docs = docRep.countByOwnerAndDeletedIsFalseAndActiveIsTrue(u);
        
        assertThat(docs, equalTo(2l));
    }    
}
