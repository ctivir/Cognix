/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class DocumentControllerTest {
    private ExtendedModelMap uiModel;

    @Before
    public void init() {
        uiModel = new ExtendedModelMap();
    }
    
    @Test
    public void testListDocuments(){
        User loggerUser = new User();
        loggerUser.setUsername("marcos");
        loggerUser.setName("marcos");
        
        List<Document> docList = new ArrayList<>();
        Document d1 = new Document();
        d1.setCreated(new DateTime());
        Document d2 = new Document();
        d2.setCreated(new DateTime());
        docList.add(d1);
        docList.add(d2);
        
        Page<Document> pageList = new PageImpl<>(docList);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        DocumentsController controller = new DocumentsController();
        DocumentService docService = mock(DocumentService.class);     
        Pageable limit = new PageRequest(0,9);
        when(docService.getPage(limit)).thenReturn(pageList);
        
        ReflectionTestUtils.setField(controller, "docService", docService);
        
        String result = controller.main(uiModel);
        assertThat(result, equalTo("documents/"));
       
        Page<Document> pageDocs = (Page<Document>) uiModel.get("documents");
        List<Document> docs = pageDocs.getContent();
        assertThat(docs, notNullValue());
        assertThat(docs, hasSize(2)); //a ordem já é testada no service
        assertThat(pageDocs.getTotalPages(), equalTo(1)); //Testando paginação
        
        String currentUser = (String) uiModel.get("currentUser");
        assertThat(currentUser, equalTo(loggerUser.getName()));
        
        String permDocAdmin = (String) uiModel.get("permDocAdmin");
        String permCreateDoc = (String) uiModel.get("permCreateDoc");
        assertThat(permDocAdmin, equalTo(User.MANAGE_DOC));
        assertThat(permCreateDoc, equalTo(User.CREATE_DOC));
        
    }
}
