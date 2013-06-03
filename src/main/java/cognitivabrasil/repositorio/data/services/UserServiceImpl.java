/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.data.services;

import cognitivabrasil.repositorio.data.entities.User;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author marcos
 */
@Service("UserService")
public class UserServiceImpl extends AbstractServiceImpl<User> implements UserService, UserDetailsService {

    static Logger log = Logger.getLogger(UserServiceImpl.class.getName());

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
        return (User) this.sessionFactory.getCurrentSession().createQuery("from User where login = :login").setString("login", login).uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        log.debug("Trying to get user \"" + login + "\"");
        User d = null;
        try {
            d = (User) this.sessionFactory.getCurrentSession().createQuery("from User where login = :login").setString("login", login).uniqueResult();
        } catch (Exception e) {
            log.error("Erro ao carregar usuário", e);
        }
        if (d == null) {
            log.debug("No such user " + login);
            throw new UsernameNotFoundException(
                    "No such user: " + login);
        }
        return d;
    }
}