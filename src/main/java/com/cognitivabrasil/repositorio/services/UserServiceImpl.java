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
import com.cognitivabrasil.repositorio.data.repositories.DocumentRepository;
import com.cognitivabrasil.repositorio.data.repositories.UserRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Service("UserService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRep;

    @Autowired
    private DocumentRepository docRep;

    @Override
    public User authenticate(String login, String password) {

        if (login == null) {
            return null;
        }

        User u = get(login);
        if (u != null && u.authenticate(password)) {
            return u;
        }
        return null;
    }

    @Override
    public User get(String login) {
        return userRep.findByUsername(login);
    }

    @Override
    public User get(int id) {
        return userRep.findOne(id);
    }

    @Override
    public List<User> getAll() {
        return userRep.findByDeletedIsFalse();
    }

    @Override
    public void save(User u) {
        userRep.save(u);
    }

    @Override
    public void delete(User u) {
        if (u == null) {
            throw new DataAccessException("This user can not be null") {
            };
        }
        if (isLastAdmin(u)) {
            throw new IllegalStateException("Não é permitido deletar o último administrador do sistema.");
        }
        //testa se possui algum documento
        if (hasDocument(u)) {
            u.setDeleted(true);
            userRep.save(u);
        } else {
            userRep.delete(u);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        LOG.debug("Trying to get user \"" + login + "\"");
        User d = get(login);

        if (d == null) {
            LOG.debug("No such user " + login);
            throw new UsernameNotFoundException(
                    "No such user: " + login);
        }
        return d;
    }

    @Override
    public boolean hasDocument(User u) {
        return docRep.countByOwnerAndDeletedIsFalseAndActiveIsTrue(u) > 0;
    }

    @Override
    public List<User> getDeleted() {
        return userRep.findByDeletedIsTrue();
    }

    @Override
    public void activate(User u) {
        if (u == null) {
            throw new DataAccessException("This user can not be null") {
            };
        }
        u.setDeleted(false);
        userRep.save(u);
    }

    @Override
    public boolean isLastAdmin(User u) {
        if (!u.isRoot()) {
            return false;
        }
        return userRep.countByRole(User.ROLE_ROOT) < 2;
    }
}
