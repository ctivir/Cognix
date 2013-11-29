package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Relation;
import com.cognitivabrasil.repositorio.web.DocumentsController;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.data.entities.Document;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import static org.hamcrest.Matchers.*;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author cei
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Ignore
public class DocumentsControllerTestIgnore extends AbstractTransactionalJUnit4SpringContextTests {

    DocumentService docService;
    Properties config;
    private ExtendedModelMap uiModel;    
    static final String FILE1 = "src/test/resources/obaa1.xml";
    @Autowired
    DocumentsController controller;
    
    @Before
    public void initMeta() {
        
        uiModel = new ExtendedModelMap();
        docService = mock(DocumentService.class);
        config = mock(Properties.class);
    }
    
    @Test
    public void RelationTest() throws IOException {
        int versionOf = 1;
        Document d = new Document();
        
         
        // Initialize list of alunos for mocked alunoService
        try {            
            String obaaXml = FileUtils.readFileToString(new File(FILE1));
            d.setObaaXml(obaaXml);
            
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados do XML de testes."+ e);
        }  
        
        OBAA meta = d.getMetadata();
        assertThat(meta.getGeneral().getTitles().get(0), equalTo("Título 1"));
        
        when(docService.get(versionOf)).thenReturn(d);
        
        ReflectionTestUtils.setField(controller, "docService", docService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        when(config.getProperty("Repositorio.port", "8080")).thenReturn("8080");
        when(config.getProperty("Repositorio.hostname")).thenReturn("localhost");
        when(config.getProperty("Repositorio.rootPath", "/repositorio")).thenReturn("/repositorio");        
        ReflectionTestUtils.setField(controller, "config", config);
        
        String result = controller.newVersionOf(uiModel, versionOf);
        assertThat(result, equalTo("documents/new"));
        
        
        Document dv = (Document) uiModel.get("doc");        
        assertThat(dv, notNullValue());
        
        OBAA versionMeta = dv.getMetadata();
        
        List <String> kinds = new ArrayList();
        
        for (Relation relation:versionMeta.getRelations()){
            kinds.add(relation.getKind());
        }      
        
        assertThat(kinds, hasItem("hasVersion"));
                
    }
}
