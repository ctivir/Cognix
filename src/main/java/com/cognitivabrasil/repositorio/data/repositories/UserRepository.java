/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

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
    
    public long countByRole(String role);
}
