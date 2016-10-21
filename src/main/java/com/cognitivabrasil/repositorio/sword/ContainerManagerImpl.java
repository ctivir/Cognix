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
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.util.Config;
import org.apache.log4j.Logger;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.UriRegistry;
import java.util.Map;
import java.util.Properties;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.swordapp.server.Deposit;
import spring.ApplicationContextProvider;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 *
 * This class contains the method signatures for retrieving and updating an
 * existing object on the server
 *
 */
public class ContainerManagerImpl implements ContainerManager {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ContainerManagerImpl.class);

    private static Properties config;

    /**
     * edit iri
     */
    private static final String EDITIRI = Config.getUrl(config) + "/edit?id=";

    UrlManager urlManager;

    /* return derivate id */
    private int getID(String editIRI) {
        return new Integer(editIRI.substring(EDITIRI.length()));
    }

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

    /**
     *
     * @param editIRI
     * @param accept
     * @param auth
     * @param sc
     * @return
     * @throws SwordServerException
     * @throws SwordError
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt getEntry(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration sc)
            throws SwordServerException, SwordError, SwordAuthException {
        String globalId = urlManager.getTargetIdentifier();
        Files files = null;
        if (checkAuthCredentials(auth)) {
            ReceiptGenerator receiptGenerator = new ReceiptGenerator();
            String baseUrl = urlManager.getHostnamePlusBaseUrlPath(editIRI);
            DepositReceipt depositReceipt = receiptGenerator.createDatasetReceipt(baseUrl, files);
            if (depositReceipt != null) {
                return depositReceipt;
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not generate deposit receipt.");
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine target type from URL: " + editIRI);
        }
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt replaceMetadata(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt replaceMetadataAndMediaResource(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt addMetadataAndResources(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt addMetadata(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        return this.replaceMetadata(editIRI, deposit, auth, sc);
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt addResources(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        IRI id = new IRI(editIRI);
        if (checkAuthCredentials(auth)) {
            try {
                log.debug("multipart: " + deposit.isMultipart());
                log.debug("binary only: " + deposit.isBinaryOnly());
                log.debug("entry only: " + deposit.isEntryOnly());
                log.debug("in progress: " + deposit.isInProgress());
                log.debug("metadata relevant: " + deposit.isMetadataRelevant());
                log.debug("Salvar o documento: " + editIRI + " | " + deposit + " | " + sc);
            } catch (Exception e) {
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        } else {
            throw new SwordAuthException("Você não tem permissão para gravar o aquivo!");
        }
        return null;
    }

    /**
     *
     * @param editIRI
     * @param auth
     * @param sc
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public void deleteContainer(String editIRI, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        IRI id = new IRI(editIRI);
        int documentID = -1;
        if (checkAuthCredentials(auth)) {
            try {
                if (documentID != -1) {
                    log.debug("Você tentou deletar um documento com id: " + documentID);
                } else {
                    FileUtils.forceDelete(new java.io.File(editIRI));
                    log.debug("Documento com id: " + documentID + "deletado");
                }
            } catch (Exception e) {
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        } else {
            throw new SwordAuthException("Você não tem permissão para deletar documentos!");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param editIRI
     * @param deposit
     * @param auth
     * @param config
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt useHeaders(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

    /**
     *
     * @param editIRI
     * @param accept
     * @param auth
     * @param config
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public boolean isStatementRequest(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        //TODO check accept Map for content-type that we support for statements
        return false;
    }
}