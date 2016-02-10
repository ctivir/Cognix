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
