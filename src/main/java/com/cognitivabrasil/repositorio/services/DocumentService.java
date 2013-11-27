/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Interface for documents.
 *
 * This will be injected by Spring, and can be used to do operations on FEB
 * documents.
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
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
    void delete(Document d);

    void deleteAll();

    void deleteFromDatabase(Document d);
    
    /**
     * Deletes all documents created three hours ago or more.
     */
    void deleteEmpty();
    
}
