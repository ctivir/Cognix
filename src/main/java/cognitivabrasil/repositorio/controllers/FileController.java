/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cognitivabrasil.obaa.Technical.Format;
import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import cognitivabrasil.repositorio.data.services.DocumentsService;
import cognitivabrasil.repositorio.data.services.FilesService;
import cognitivabrasil.util.Message;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@RequestMapping("/files")
@Controller
public class FileController {

    org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileController.class);
    @Autowired
    FilesService fileService;
    @Autowired
    private DocumentsService documentsService;
    private Files files = null;
    private static final String RESP_SUCCESS = "{\"jsonrpc\" : \"2.0\", \"result\" : \"success\", \"id\" : \"id\"}";
    private static final String RESP_ERROR = "{\"jsonrpc\" : \"2.0\", \"error\" : {\"code\": 101, \"message\": \"Falha ao abrir o input stream.\"}, \"id\" : \"id\"}";
    public static final String FILEPATH = "/var/cognitiva/repositorio";    

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String add(Model model) {
        return "files/new";
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

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") Integer id) {

        cognitivabrasil.repositorio.data.entities.Files f = fileService.get(id);

        if (f == null) {
            return new Message(Message.ERROR, "O arquivo não foi encontrado na base de dados!", "upload");
        }

        Document doc = f.getDocument();
        String fileFormat = f.getContentType();
        Format format = new Format(fileFormat);

        doc.getMetadata().getTechnical().getFormat().remove(format.getText());

        try {
            fileService.delete(f);
        } catch (RuntimeException e) {
            return new Message(Message.WARN, "O arquivo não foi encontrado no disco, mas foi removido da base de dados!", "upload");
        }
        return new Message(Message.SUCCESS, "Arquivo excluido com sucesso", "upload");
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletResponse response,
            @RequestBody MultipartFile file, @RequestParam String name,
            @RequestParam int chunks, @RequestParam int chunk, @RequestParam int docId)
            throws IOException {
        if (files == null) {
            files = new Files();
            files.setSize(0L);
        }

        String responseString = RESP_SUCCESS;

        File uploadFile = new File(FILEPATH, name);

        BufferedOutputStream bufferedOutput;
        try {
            bufferedOutput = new BufferedOutputStream(new FileOutputStream(uploadFile, true));
            byte[] data = file.getBytes();
            bufferedOutput.write(data);
            bufferedOutput.close();
        } catch (IOException e) {
            log.error("Erro ao salvar o arquivo.", e);
            files = null;
            responseString = RESP_ERROR;
            throw e;
        } finally {
            files.setName(file.getOriginalFilename());
            files.setRandomName(name);
            files.setContentType(file.getContentType());
            files.setPartialSize(file.getSize());
        }

        if (chunk == chunks - 1) {
            files.setLocation(FILEPATH + "/" + name);
            files.setDocument(documentsService.get(docId));
            fileService.save(files);
            file = null;
        }

        return responseString;
    }
}
