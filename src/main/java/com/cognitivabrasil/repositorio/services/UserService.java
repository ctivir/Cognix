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

import com.cognitivabrasil.repositorio.data.entities.User;
import java.util.List;

/**
 *
 * @author marcos
 */
public interface UserService {

    /**
     *
     * @return All Users
     */
    List<User> getAll();

    /**
     * Creates or updates the User.
     *
     * @param u user to be created or updated.
     */
    void save(User u);

    /**
     * Deletes a user.
     *
     * @param u user to be deleted
     */
    void delete(User u);

    /**
     * Gets a specific user by id.
     *
     * @param id id of the user
     * @return User
     */
    User get(int id);

    /**
     * Gets a specific user by login.
     *
     * @param login Login of the user
     * @return User if found, otherwise null
     */
    User get(String login);

    /**
     * Gets a specific user only if the password matches.
     *
     * @param login Username of the user
     * @param password Password supplied
     * @return User object if password matches login, null otherwise
     */
    User authenticate(String login, String password);
    
    /**
     * Verify if the user is owner of any document
     * @param u User
     * @return true if have document and otherwise false.
     */
    public boolean hasDocument(User u);
    
    
     /**
     *
     * @return All deleted users
     */
    public List<User> getDeleted();
    
    /**
     * Ative the user deleted
     * @param u User
     */
    public void activate(User u);
    
    /**
     * Tests if the user is the last system administrator.
     * @param u User
     * @return boolean
     */
    public boolean isLastAdmin(User u);
}
