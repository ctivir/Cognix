/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.util.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */

public class FileControllerTest {

    private final List<Files> files = new ArrayList<>();
    private FileService fileService;
    private ExtendedModelMap uiModel;
    
    /**
     * Cria um mock FileService para se usado nos testes.
     */
    @Before
    public void init() {
        // Initialize list of files for mocked fileService
        Files file = new Files();
        file.setId(1);
        file.setName("teste.txt");
        files.add(file);

        Files f2 = new Files();
        f2.setId(2);
        f2.setName("foto.jpg");
        files.add(f2);

        fileService = mock(FileService.class);

        uiModel = new ExtendedModelMap();
    }
    
    /**
     * Helper para criar um alunosController já injetado com o mock do
     * alunoService.
     *
     * @return
     */
    private FileController mockFiles() {
        FileController fileController = new FileController();
        ReflectionTestUtils.setField(fileController, "fileService",
                fileService);
        return fileController;
    }

    
    @Test
    public void newTest(){
        FileController fileController = new FileController();
        String result = fileController.add(uiModel);
        assertThat(result, equalTo("files/new"));
    }
  

    
    
    @Test
    public void testUploadFile() throws IOException, ServletException, FileUploadException {    
        HttpServletResponse response = new MockHttpServletResponse();
        
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest() ;
        assertThat(request.getFileNames().hasNext(),equalTo(false)) ;
        assertThat( request.getFile("file1") , equalTo(null) ) ;
        assertThat( request.getFile("file2") , equalTo(null) ) ;
        assertThat( request.getFileMap().isEmpty() , equalTo(true) );
        
        request.setContentType("multipart/form-data; boundary=-----1234");
        request.setCharacterEncoding("text/plain");
        request.setContent("algo".getBytes());
        request.setMethod("POST");
       
        
        MockMultipartFile mockMultipartFile = new MockMultipartFile("content", "test.txt", "text/plain", "HelloWorld".getBytes());
        request.addFile( mockMultipartFile );
        
        FileController fileController = mockFiles();

        String result = fileController.upload(request, response);
        
        assertThat(result, equalTo("{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}"));
        //testes
    
    }
    
    @Test
    public void testUploadFileErroNotMultipart() throws IOException, ServletException, FileUploadException {    
        HttpServletResponse response = new MockHttpServletResponse();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("text/plain");
        request.setContent("algo".getBytes());
        request.setMethod("POST");
        
        FileController fileController = mockFiles();

        String result = fileController.upload(request, response);
        
        assertThat(result, equalTo("{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Falha ao abrir o input stream.\"}, \"id\" : \"id\"}"));
        
    }
    // Warning, this test doesn't work how it's supposed to.
    @Test
    public void testGetThumbnail()throws IOException {
        
        HttpServletResponse response = new MockHttpServletResponse();
        HttpServletResponse response2 = new MockHttpServletResponse();
        FileController fileController = mockFiles();
        
        // proves response and response2 are not comitted yet.
        Assert.assertFalse(response.isCommitted());
        Assert.assertFalse(response2.isCommitted());
      
         // tests id = null. 
        fileController.getThumbnail(null, response);
        Assert.assertTrue(response.isCommitted());
        assertThat(HttpServletResponse.SC_NOT_FOUND, equalTo(response.getStatus()));
              
        // tests id = 1      
        Long id = 1L;         
        
        Assert.assertFalse(response2.isCommitted());
        fileController.getThumbnail(id, response2);
        Assert.assertTrue(response2.isCommitted());
        assertThat(HttpServletResponse.SC_CREATED, equalTo(response2.getStatus()));
                 
        // proves response2 is only commited after flushbuffer.
               
       }
    
    @Test
    public void testGetFile()throws IOException {
        
        HttpServletResponse response = new MockHttpServletResponse();
        HttpServletResponse response2 = new MockHttpServletResponse();
        FileController fileController = mockFiles();
        
        // proves response and response2 are not comitted yet.
        Assert.assertFalse(response.isCommitted());
        Assert.assertFalse(response2.isCommitted());
      
         // tests id = 0. 
        int id = 0; 
        fileController.getFile(id, response);
        Assert.assertTrue(response.isCommitted());
        assertThat(HttpServletResponse.SC_GONE, equalTo(response.getStatus()));
              
        // tests id = 1      
        id = 1;         
        Assert.assertFalse(response2.isCommitted());
        fileController.getFile(id, response2);
        Assert.assertTrue(response2.isCommitted());
        assertThat(HttpServletResponse.SC_GONE, equalTo(response2.getStatus()));
                 
        // proves response2 is only commited after flushbuffer.
               
       }
    
    
    
}
