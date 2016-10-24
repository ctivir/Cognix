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
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
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
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.swordapp.server.Deposit;
import spring.ApplicationContextProvider;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 * @author Igor Pires Ferreira <ipferreira@inf.ufrgs.br>
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

    private final UserService userService;

    private final DocumentService docService;

    private final FileService fileService;

    /**
     * edit iri
     */
    private static final String EDITIRI = Config.getUrl(config) + "/edit?id=";

    UrlManager urlManager;

    /* return derivate id */
    private int getID(String editIRI) {
        return new Integer(editIRI.substring(EDITIRI.length()));
    }

    public ContainerManagerImpl() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        userService = ctx.getBean(UserService.class);
        docService = ctx.getBean(DocumentService.class);
        fileService = ctx.getBean(FileService.class);
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
        String[] s = editIRI.split("/");
        if (checkAuthCredentials(auth)) {
            try {
                Files f = fileService.get(Integer.parseInt(s[s.length - 1]));
                fileService.deleteFile(f);
                
            } catch (IOException ex) {
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
        //Todo: verificar a IRI pra ver se é do domínio onde a aplicação está
        IRI id = new IRI(editIRI);
        String[] s = editIRI.split("/");
        if (checkAuthCredentials(auth)) {
            try {
                Files f = fileService.get(Integer.parseInt(s[s.length - 1]));
                fileService.deleteFile(f);
                log.debug("Documento com id: " + f.getId() + "deletado");
            } catch (NumberFormatException | IOException e) {
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        } else {
            throw new SwordAuthException("Você não tem permissão para deletar documentos!");
        }
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
