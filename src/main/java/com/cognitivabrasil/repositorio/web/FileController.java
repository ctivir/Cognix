/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cognitivabrasil.obaa.Technical.Format;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.util.Message;
import com.cognitivabrasil.repositorio.util.Config;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 *
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@RequestMapping("/files")
@Controller
public class FileController {

    private static final Logger LOG = Logger.getLogger(FileController.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private DocumentService documentsService;
    private Files file = null;
    private int chunk;
    private int chunks;
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Falha ao abrir o input stream.\"}, \"id\" : \"id\"}";
    public static final String DEFAULT_THUMBNAIL_PATH = "./src/main/resources/default-thumbnail.png";
    
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String add(Model model) {
        return "files/new";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void getFile(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Files f = fileService.get(id);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_GONE, "O arquivo solicitado não foi encontrado.");
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
                response.sendError(HttpServletResponse.SC_GONE, "O arquivo solicitado não foi encontrado.");
                LOG.error("O arquivo solicitado não foi encontrado.", fe);
                } catch (IOException ex) {
                LOG.error("Error writing file to output stream. Filename was '" + fileName + "'");
                throw ex;
            }
        }
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") Integer id) {

        com.cognitivabrasil.repositorio.data.entities.Files f = fileService.get(id);

        if (f == null) {
            return new Message(Message.ERROR, "O arquivo não foi encontrado na base de dados!", "upload");
        }

        Document doc = f.getDocument();
        String fileFormat = f.getContentType();
        Format format = new Format(fileFormat);

        doc.getMetadata().getTechnical().getFormats().remove(format);

        try {
            fileService.deleteFile(f);
        } catch (IOException e) {
            String errorMsg = "O arquivo não foi encontrado no disco, mas foi removido da base de dados!";
            LOG.error(errorMsg, e);
            
            return new Message(Message.WARN, errorMsg, "upload");
        }
        return new Message(Message.SUCCESS, "Arquivo excluido com sucesso.", "upload");
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, org.apache.commons.fileupload.FileUploadException {
        if (file == null) {
            file = new Files();
            file.setSizeInBytes(0L);
        }

        Integer docId = null;
        String docPath = null;
        String responseString = RESP_SUCCESS;
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            try {
                ServletFileUpload x = new ServletFileUpload(new DiskFileItemFactory());
                List<FileItem> items = x.parseRequest(request);

                for (FileItem item : items) {
                    InputStream input = item.getInputStream();

                    // Handle a form field.
                    if (item.isFormField()) {
                        String attribute = item.getFieldName();
                        String value = Streams.asString(input);

                        switch (attribute) {
                            case "chunks":
                                this.chunks = Integer.parseInt(value);
                                break;
                            case "chunk":
                                this.chunk = Integer.parseInt(value);
                                break;
                            case "filename":
                                file.setName(value);
                                break;
                            case "docId":
                                if (value.isEmpty()) {
                                    throw new org.apache.commons.fileupload.FileUploadException("Não foi informado o id do documento.");
                                }
                                docId = Integer.parseInt(value);
                                docPath = Config.FILE_PATH + "/" + docId;
                                File documentPath = new File(docPath);
                                // cria o diretorio
                                documentPath.mkdirs();

                                break;
                        }

                    } // Handle a multi-part MIME encoded file.
                    else {
                        try {

                            File uploadFile = new File(docPath, item.getName());
                            BufferedOutputStream bufferedOutput;
                            bufferedOutput = new BufferedOutputStream(new FileOutputStream(uploadFile, true));

                            byte[] data = item.get();
                            bufferedOutput.write(data);
                            bufferedOutput.close();
                        } catch (Exception e) {
                            LOG.error("Erro ao salvar o arquivo.", e);
                            file = null;
                            throw e;
                        } finally {
                            if (input != null) {
                                try {
                                    input.close();
                                } catch (IOException e) {
                                    LOG.error("Erro ao fechar o ImputStream", e);
                                }
                            }

                            file.setName(item.getName());
                            file.setContentType(item.getContentType());
                            file.setPartialSize(item.getSize());
                        }
                    }
                }

                if ((this.chunk == this.chunks - 1) || this.chunks == 0) {
                    file.setLocation(docPath + "/" + file.getName());
                    if (docId != null) {
                        file.setDocument(documentsService.get(docId));
                    }
                    fileService.save(file);
                    file = null;
                }
            } catch (org.apache.commons.fileupload.FileUploadException | IOException | NumberFormatException e) {
                responseString = RESP_ERROR;
                LOG.error("Erro ao salvar o arquivo", e);
                file = null;
                throw e;
            }
        } // Not a multi-part MIME request.
        else {
            responseString = RESP_ERROR;
        }

        response.setContentType("application/json");
        byte[] responseBytes = responseString.getBytes();
        response.setContentLength(responseBytes.length);
        ServletOutputStream output = response.getOutputStream();
        output.write(responseBytes);
        output.flush();
        return responseString;
    }

    @RequestMapping(value = "/{id}/thumbnail", method = RequestMethod.GET)
    public void getThumbnail(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {

        if (id == null || id == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "O arquivo solicitado não foi encontrado.");
        } else {
            String fileName = Config.FILE_PATH + id + "/thumbnail";

            try {
                // get your file as InputStream
                InputStream is = new FileInputStream(new File(fileName));

                response.setHeader("Content-Disposition", "attachment; filename= thumbnail" + id);
                response.setStatus(HttpServletResponse.SC_CREATED);
                // copy it to response's OutputStream
                IOUtils.copy(is, response.getOutputStream());

                response.flushBuffer();

            } catch (FileNotFoundException fe) {
                // get your file as InputStream
               
                InputStream is = new FileInputStream(new File(DEFAULT_THUMBNAIL_PATH));
                
                response.setHeader("Content-Disposition", "attachment; filename=default-thumbnail.png");
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                response.setStatus(HttpServletResponse.SC_CREATED);
                // copy it to response's OutputStream
                IOUtils.copy(is, response.getOutputStream());

                response.flushBuffer();
                LOG.error("Imagen solicitada não foi encontrada.", fe);
            } catch (IOException ex) {
                LOG.error("Error writing file to output stream. Filename was '" + fileName + "'");
                throw ex;
            }
        }
    }

}
