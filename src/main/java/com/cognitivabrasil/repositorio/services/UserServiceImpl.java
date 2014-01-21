/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.data.repositories.UserRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    static Logger log = Logger.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRep;

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
        if (u != null) {
            u.setDeleted(true);
        }
        userRep.save(u);
    }

    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        log.debug("Trying to get user \"" + login + "\"");
        User d = get(login);

        if (d == null) {
            log.debug("No such user " + login);
            throw new UsernameNotFoundException(
                    "No such user: " + login);
        }
        return d;
    }
}