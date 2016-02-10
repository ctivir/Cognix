/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
 
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// TODO: Auto-generated Javadoc
/**
 * Interface for documents.
 *
 *
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 * @author Marcos Nunes <marcos@cognitivabrasil.com.br>
 */
public interface DocumentService {

    /**
     * Gets the document with the specified OBAAEntry.
     *
     * @param obaaEntry the obaa entry
     * @return the document with the corresponding obaaEntry
     */
    Document get(String obaaEntry);

    /**
     * Need to call flush on session after saving many documents. Need to set
     * one repository or one federation before.
     *
     * @param d the Document
     */
    void save(Document d);


    /**
     * Gets all NON-DELETED documents present in the System.
     *
     * Use with extreme care, as it might return to many results.
     *
     * @return All the documents that are not deleted
     */
    List<Document> getAll();
    
    /**
     * Gets all NON-DELETED ,on selected page, documents present in the System.
     *
     * @return All the documents that are not deleted and are in the selected page
     */
    Page<Document> getPage(Pageable pageable);
    
     /**
     * Gets all NON-DELETED documents, by the subject, present in the System.
     *
     * Use with extreme care, as it might return to many results.
     *
     * @return Documents by the subject passed and that are not deleted
     */
    List<Document> getBySubject(Subject s);
    
    /**
     * Gets all NON-DELETED documents, on selected page, by the subject, present in the System.
     *
     * Use with extreme care, as it might return to many results.
     *
     * @return Documents by the subject passed and that are not deleted and are in the selected page
     */
    Page<Document> getPageBySubject(Subject s, Pageable pageable);
    
    
    /**
     * Gets the document by ID.
     *
     * @param i the Id
     * @return the Document with the corresponding ID.
     */
    Document get(int i);


    /**
     * Delete a document.
     *
     * It will mark the document as deleted, and remove all related files. It
     * does NOT actually remove the row from the database.
     *
     * @param d the document to be deleted.
     */
    void delete(Document d) throws IOException;

    void deleteAll();

    void deleteFromDatabase(Document d);
    
    /**
     * Deletes all documents created three hours ago or more.
     */
    void deleteEmpty();
    
    /**
     * Count number of active documents and not deleted.
     */
    long count();    
}
