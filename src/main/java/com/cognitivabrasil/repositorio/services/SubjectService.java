/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Interface for documents.
 *
 * This will be injected by Spring, and can be used to do operations on FEB
 * documents.
 *
 * @author Alan Velasques Santos <alanvelasques@gmail.com>
 */
public interface SubjectService {

    /**
     * Gets all NON-DELETED Subjects present in the System.
     *
     * Use with extreme care, as it might return to many results.
     *
     * @return All the subjects that are not deleted
     */
    List<Subject> getAll();
    
    /**
     * Gets NON-DELETED subjects present in the System by name.
     *
     * Use with extreme care, as it might return to many results.
     *
     * @return List of subjects that are not deleted and has the name passed
     */
    Subject getSubjectByName(String name);

    
}
