/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.services;

import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.data.repositories.SubjectRepository;
import com.cognitivabrasil.repositorio.services.SubjectService;

import java.util.List;
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
 * Integration tests of the DocumentService
 * 
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SubjectServiceIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SubjectService subService;
    @Autowired
    private SubjectRepository subRep;
    
    @Test
    public void testSubject(){
        Subject s = subService.getSubjectByName("ciencias");
        assertThat(s.getId(), equalTo(1));
    }
    
    @Test
    public void testAllSubjects(){
        List<Subject> s = subService.getAll();
        assertThat(s, hasSize(3));
    }
        
}
