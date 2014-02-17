/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.services;

import ORG.oclc.oai.server.catalog.OaiDocumentService;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.data.repositories.DocumentRepository;
import com.cognitivabrasil.repositorio.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * The Class DocumentServiceImpl.
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 */
@Service
public class DocumentServiceImpl implements DocumentService, OaiDocumentService {

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
    public Page<Document> getPage(Pageable pageable) {                                
        return docRep.findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(pageable);
    }
    
    @Override
    public List<Document> getBySubject(Subject s) {
        return docRep.findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(s);
    }
    
    @Override
    public Page<Document> getPageBySubject(Subject s, Pageable pageable) {
        return docRep.findBySubjectAndDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc(s, pageable);
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

	@Override
	public Iterator find(Date from, Date until, int oldCount, int maxListSize) {	
		PageRequest pr = new PageRequest(oldCount/maxListSize, maxListSize, Sort.Direction.ASC, "created");
		
		if(from != null && until != null) {
			return docRep.betweenInclusive(new DateTime(from), add1Second(new DateTime(until)), pr).iterator();
		}
		else if (from != null && until == null) {
			return docRep.from(new DateTime(from), pr).iterator();
		}
		else if(from == null && until != null) {
			return docRep.until(add1Second(new DateTime(until)), pr).iterator();

		}
		else {
			return docRep.all(
					pr).iterator();
		}
	}



	@Override
	public int count(Date from, Date until) {
		if(from != null && until != null) {
			return docRep.countBetweenInclusive(new DateTime(from), add1Second(new DateTime(until)));
		}
		else if (from != null && until == null) {
			return docRep.countFrom(new DateTime(from));
		}
		else if(from == null && until != null) {
			return docRep.countUntil(add1Second(new DateTime(until)));

		}
		else {
			return (int) docRep.count();
		}
		
	}
	
	private DateTime add1Second(DateTime dateTime) {
		return dateTime.plusSeconds(1);
	}

}
