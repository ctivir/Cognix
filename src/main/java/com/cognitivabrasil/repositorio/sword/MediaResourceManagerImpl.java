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

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.swordapp.server.UriRegistry;
import spring.ApplicationContextProvider;

public class MediaResourceManagerImpl implements MediaResourceManager {

    private static final Logger log = Logger.getLogger(MediaResourceManagerImpl.class);

    /**
     * check authentication
     */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());

        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        UserService userService = ctx.getBean(UserService.class);
        User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        return (user != null && user.hasPermission(User.CREATE_DOC));
    }

    @Override
    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {

        boolean getMediaResourceRepresentationSupported = false;
        if (getMediaResourceRepresentationSupported) {
            if (checkAuthCredentials(auth)) {
                InputStream fixmeInputStream = new ByteArrayInputStream("FIXME: replace with zip of all dataset files".getBytes());
                String contentType = "application/zip";
                String packaging = UriRegistry.PACKAGE_SIMPLE_ZIP;
                boolean isPackaged = true;
                MediaResource mediaResource = new MediaResource(fixmeInputStream, contentType, packaging, isPackaged);
                return mediaResource;
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Couldn't dermine target type or identifier from URL: " + uri);
        }
    }

    @Override
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Replacing the files of a dataset is not supported. Please delete and add files separately instead.");
    }

    @Override
    public void deleteMediaResource(String uri, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
    }

    @Override
    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        IRI mediaUri = new IRI(uri);
        if (checkAuthCredentials(auth)) {
            Files file = new Files();
            try {
                return file(deposit, auth, mediaUri);
            } catch (Exception e) {
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        }
        return null;
    }
}
