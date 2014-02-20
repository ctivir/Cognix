/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitivabrasil.repositorio.data.services;

import ORG.oclc.oai.server.catalog.OaiDocumentService;
import com.cognitivabrasil.repositorio.data.entities.Document;
import java.util.Iterator;
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
public class OaiDocumentServiceIT extends AbstractTransactionalJUnit4SpringContextTests{
    @Autowired
    OaiDocumentService oaiService;
    
    @Test
    public void testCount(){
       int docs = oaiService.count(null, null);
        assertThat(docs, equalTo(5));
    }
    
    @Test
    public void testCountBetween(){
        DateTime from = DateTime.parse("2013-05-08T03:00:01.000Z");
        DateTime until = DateTime.parse("2013-08-21T08:10:00.000Z");
        int docs = oaiService.count(from.toDate(), until.toDate());
        assertThat(docs, equalTo(2));
        
    }
    
    @Test
    public void testCountUntil(){
        
        DateTime until = DateTime.parse("2013-05-08T03:10:01.000Z");
        int docs = oaiService.count(null, until.toDate());
        assertThat(docs, equalTo(3));
    }
    
    @Test
    public void testCountFrom(){
        
        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        int docs = oaiService.count(from.toDate(), null);
        assertThat(docs, equalTo(3));
    }
    
    @Test
    public void testFindAllPageable(){
        Iterator i = oaiService.find(null, null, 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(2));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(1));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(null, null, 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(null, null, 4, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(7));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(null, null, 6, 2);
        assertThat(i.hasNext(), equalTo(false));
    }
    
    @Test
    public void testFindFrom(){
        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        Iterator i = oaiService.find(from.toDate(), null, 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(from.toDate(), null, 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(7));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(from.toDate(), null, 4, 2);
        assertThat(i.hasNext(), equalTo(false));
    }
    
    @Test
    public void testFindUntil(){
        DateTime until = DateTime.parse("2013-05-08T03:10:01.000Z");
        
        Iterator i = oaiService.find(null, until.toDate(), 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(2));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(1));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(null, until.toDate(), 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        assertThat(i.hasNext(), equalTo(false));
    }
    
    
    @Test
    public void testFindBetween(){
        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        DateTime until = DateTime.parse("2013-08-21T08:10:00.000Z");
        
        Iterator i = oaiService.find(from.toDate(), until.toDate(), 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));
        
        i = oaiService.find(from.toDate(), until.toDate(), 2, 2);
        assertThat(i.hasNext(), equalTo(false));
    }

}
