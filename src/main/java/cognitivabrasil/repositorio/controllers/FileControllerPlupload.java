/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers;

import cognitivabrasil.obaa.Technical.Technical;
import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import cognitivabrasil.repositorio.data.services.FilesService;
import cognitivabrasil.util.Message;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class FileControllerPlupload {
    @Autowired
    private FilesService fileService;
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    public static final String FILEPATH = "/var/cognitiva/repositorio";
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Falha ao abrir o input stream.\"}, \"id\" : \"id\"}";
    public static final String JSON = "application/json";
    public static final int BUF_SIZE = 2 * 1024;
    public static final String FileDir = FILEPATH;
    private int chunk;
    private int chunks;
    private Files file = null;


    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String add(Model model) {
        return "files/new";
    }
    
    
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, org.apache.commons.fileupload.FileUploadException {
        if (file == null) {
            file = new Files();
            file.setSize(0L);
        }

        String responseString = RESP_SUCCESS;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            try {
                ServletFileUpload x = new ServletFileUpload(new DiskFileItemFactory()) ;
                List<FileItem> items = x.parseRequest(request);

                for (FileItem item : items) {
                    InputStream input = item.getInputStream();

                    // Handle a form field.
                    if (item.isFormField()) {
                        String fileName = item.getFieldName();
                        String value = Streams.asString(input);

                        switch (fileName) {
                            case "name":
                                file.setRandomName(value);
                                break;
                            case "chunks":
                                this.chunks = Integer.parseInt(value);
                                break;
                            case "chunk":
                                this.chunk = Integer.parseInt(value);
                                break;
                        }

                    } // Handle a multi-part MIME encoded file.
                    else {
                        try {
                            File uploadFile = new File(FILEPATH, file.getRandomName());
                            BufferedOutputStream bufferedOutput;
                            bufferedOutput = new BufferedOutputStream(new FileOutputStream(uploadFile, true));


                            byte[] data = item.get();
                            bufferedOutput.write(data);
                            bufferedOutput.close();
                        } catch (Exception e) {
                            log.error("Erro ao salvar o arquivo.", e);
                            file = null;
                            throw e;
                        } finally {
                            if (input != null) {
                                try {
                                    input.close();
                                } catch (IOException e) {
                                    log.error("Erro ao fechar o ImputStream", e);
                                }
                            }

                            file.setName(item.getName());
                            file.setContentType(item.getContentType());
                            file.setPartialSize(item.getSize());
                        }
                    }
                }

                if (this.chunk == this.chunks - 1) {
                    file.setLocation(FILEPATH + "/" + file.getRandomName());
                    fileService.save(file);
                    file = null;
                }
            } catch (org.apache.commons.fileupload.FileUploadException | IOException | NumberFormatException e) {
                responseString = RESP_ERROR;
                log.error("Erro ao salvar o arquivo", e);
                file = null;
                throw e;
            }
        } // Not a multi-part MIME request.
        else {
            responseString = RESP_ERROR;
        }

        response.setContentType(JSON);
        byte[] responseBytes = responseString.getBytes();
        response.setContentLength(responseBytes.length);
        ServletOutputStream output = response.getOutputStream();
        output.write(responseBytes);
        output.flush();
        return responseString;
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") int id) {

        Files f = fileService.get(id);
        
        if(f == null){
            return new Message(Message.ERROR, "O arquivo não foi encontrado na base de dados!", "files");
        }
        
        try {
            //deleta do disco e depois deleta do banco
            fileService.deleteFile(f); 
            
            formatUpdate(f.getDocument());
            
        } catch (IOException e) {
            log.warn("O arquivo " + f.getLocation() + " " + f.getName() + " não foi encontrado no disco.", e);
            return new Message(Message.WARN, "O arquivo não foi encontrado no disco, mas foi removido da base de dados!", "files");
        }

        return new Message(Message.SUCCESS, "Arquivo excluido com sucesso", "files");
    }

    private void formatUpdate (Document d){
        Set<Files> filesSet = d.getFiles();
        
        Technical t = d.getMetadata().getTechnical();
        t.getFormat().clear();
        
        for (Files f: filesSet){
            t.addFormat(f.getContentType());            
        }
        
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getFile(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Files f = fileService.get(id);
        if (f == null) {
            response.sendError(410, "O arquivo solicitado não foi encontrado.");
        } else {
            String fileName = f.getLocation();

            try {
                // get your file as InputStream
                InputStream is = new FileInputStream(new File(fileName));

                response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
                response.setContentType(f.getContentType());
                // copy it to response's OutputStream
                IOUtils.copy(is, response.getOutputStream());

                response.flushBuffer();

            } catch (FileNotFoundException fe) {
                response.sendError(410, "O arquivo solicitado não foi encontrado.");
            } catch (IOException ex) {
                log.error("Error writing file to output stream. Filename was '" + fileName + "'");
                throw ex;
                //throw new RuntimeException("IOError writing file to output stream");
            }
        }
    }
    
}
