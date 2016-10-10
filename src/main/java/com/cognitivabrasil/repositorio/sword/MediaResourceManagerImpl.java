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

import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.data.repositories.FileRepository;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.services.UserService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.inject.Inject;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.MediaResource;
import org.swordapp.server.MediaResourceManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.UriRegistry;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import spring.ApplicationContextProvider;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 *
 * This class contains the method signatures for retrieving and updating the
 * content within a Container on the server
 *
 */
public class MediaResourceManagerImpl implements MediaResourceManager {

    private static final Logger log = Logger.getLogger(MediaResourceManagerImpl.class);

    private FileRepository fileRep;

    FileService fileService;

    @Inject
    UrlManager urlManager;

    private final UserService userService;
    
    public MediaResourceManagerImpl() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        userService = ctx.getBean(UserService.class);
    }
    /**
     * check authentication
     */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());
        
        User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        return (user != null && user.hasPermission(User.CREATE_DOC));
    }

    /**
     *
     * @param uri
     * @param accept
     * @param auth
     * @param config
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {

        if (checkAuthCredentials(auth)) {
            InputStream inputstream = new ByteArrayInputStream("FIXME: replace with zip of all files".getBytes());
            String contentType = "application/zip";
            String packaging = UriRegistry.PACKAGE_SIMPLE_ZIP;
            boolean isPackaged = true;
            MediaResource mediaResource = new MediaResource(inputstream, contentType, packaging, isPackaged);
            return mediaResource;
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST);
        }
    }

    /**
     *
     * @param uri
     * @param deposit
     * @param auth
     * @param config
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Replacing the files of a dataset is not supported. Please delete and add files separately instead.");
    }

    /**
     *
     * @param uri
     * @param auth
     * @param sc
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public void deleteMediaResource(String uri, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        if (checkAuthCredentials(auth)) {
            try {
                FileUtils.forceDelete(new java.io.File(uri));
            } catch (IOException e) {
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        }
    }

    /**
     *
     * @param uri
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        urlManager.processUrl(uri);
        Files file = new Files();
        String globalId = urlManager.getTargetIdentifier();
        
        if (checkAuthCredentials(auth)) {
            ReceiptGenerator receiptGenerator = new ReceiptGenerator();
            String baseUrl = urlManager.getHostnamePlusBaseUrlPath(uri);
//            file = file.getLocation(globalId);
            DepositReceipt depositReceipt = receiptGenerator.createDatasetReceipt(baseUrl, file);
            return depositReceipt;
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine target type or identifier from URL: " + uri);
        }
    }
}
