/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.data.repositories.DocumentRepository;
import com.cognitivabrasil.repositorio.util.Config;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class DocumentServiceImpl.
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository docRep;
    
    @Autowired
    private FileService filesService;

    private static final Logger LOG = Logger.getLogger(DocumentServiceImpl.class);

  
    @Override
    public Document get(String e) {
        return docRep.findByObaaEntry(e);
    }

    @Override
    public Document get(int i) {
        return docRep.findOne(i);

    }

    @Override
    public List<Document> getAll() {
        return docRep.findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc();
    }
    
    @Override
    public List<Document> getBySubject(Subject s) {
        return docRep.findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(s);
    }

    @Override
    public void delete(Document d) throws IOException {
        
        try {
            FileUtils.forceDelete(new File(Config.FILE_PATH + d.getId()));
        } catch (IOException io) {
            LOG.warn("Nao foi possivel deletar os arquivos do documento: " + d.getId() + "."
                    + "Mas o documento sera removido da base! "+ io.getMessage());
            throw io;
        } finally {
            d.getFiles().clear();
            d.setObaaXml(null);
            d.setCreated(new DateTime());
            d.isDeleted(true);
            docRep.save(d);
        }        
        
    }

    @Override
    public void deleteAll() {
        for (Document d : docRep.findAll()) {
            for (Files f : d.getFiles()) {
                try {
                    f.deleteFile();
                } catch (IOException e) {
                    LOG.error("Could not delete file", e);
                }
            }
            docRep.delete(d);
        }
    }

    @Override
    public void deleteFromDatabase(Document d) {
        docRep.delete(d);
    }


    /*
     * (non-Javadoc)
     *
     * @see modelos.DocumentosDAO#save(OBAA.OBAA, metadata.Header)
     */
    @Override
    public void save(Document d) throws IllegalStateException {
        docRep.save(d);
    }
    
    @Override
    public void deleteEmpty() {
        DateTime d = DateTime.now();
        List<Document> docs = docRep.findByCreatedLessThanAndObaaXmlIsNullAndDeletedIsFalse(d.minusHours(3));
        for(Document doc : docs){
            deleteFromDatabase(doc);            
        }
    }

}
