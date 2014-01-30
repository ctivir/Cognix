/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.data.repositories;

import com.cognitivabrasil.repositorio.data.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    @Override
    public List<User> findAll();
    
    public List<User> findByDeletedIsFalse();
    
    User findByUsername(final String username);
    
    List<User> findByDeletedIsTrue();
}
