/*
 * Copyright (c) 2016 Cognitiva Brasil Tecnologias Educacionais
 * http://www.cognitivabrasil.com.br
 *
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */
package com.cognitivabrasil.repositorio.sword;

import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.MediaResource;
import org.swordapp.server.MediaResourceManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.log4j.Logger;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.swordapp.server.UriRegistry;
import spring.ApplicationContextProvider;

public class MediaResourceManagerImpl implements MediaResourceManager {

    private static final Logger log = Logger.getLogger(MediaResourceManagerImpl.class);

    /**
     * check authentication
     */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());

        ApplicationContext ctx = ApplicationContextProvider
                .getApplicationContext();
        UserService userService = ctx.getBean(UserService.class);
        User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        return (user != null && user.hasPermission(User.CREATE_DOC));
    }

    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        log.info("getMediaResourceRepresentation: " + uri);
        IRI mediaUri = new IRI(uri);
        if (checkAuthCredentials(auth)) {
            
        }
        return null;
    }

    @Override
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Replacing the files of a dataset is not supported. Please delete and add files separately instead.");
    }

    public void deleteMediaResource(String uri, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        IRI mediaUri = new IRI(uri);
        Resource resource = new ClassPathResource("/config.properties");

        throw new SwordServerException();
    }

    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
    throws SwordError, SwordServerException, SwordAuthException {        
        IRI mediaUri = new IRI(uri);
        if (checkAuthCredentials(auth)) {
            
        }else{
            throw new SwordAuthException("Você não tem permissão para criar documentos!");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }
}
