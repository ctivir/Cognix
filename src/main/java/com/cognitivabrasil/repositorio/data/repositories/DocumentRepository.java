/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.repositories;

import com.cognitivabrasil.repositorio.data.entities.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Override
    public List<Document> findAll();
    
    public List<Document> findByDeletedIsFalseOrderByCreatedDesc();
    
    public Document findByObaaEntry(String entry);
}
