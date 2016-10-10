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
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionDepositManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import spring.ApplicationContextProvider;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class CollectionDepositManagerImpl implements CollectionDepositManager {

    private static final Logger log = Logger.getLogger(CollectionDepositManagerImpl.class);

    private final UserService userService;

    public CollectionDepositManagerImpl() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        userService = ctx.getBean(UserService.class);
    }

    /**
     *
     * @param string
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt createNew(String string, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        if (checkAuthCredentials(auth)) {
            log.debug("multipart: " + deposit.isMultipart());
            log.debug("binary only: " + deposit.isBinaryOnly());
            log.debug("entry only: " + deposit.isEntryOnly());
            log.debug("in progress: " + deposit.isInProgress());
            log.debug("metadata relevant: " + deposit.isMetadataRelevant());
            log.debug("Salvar o documento: " + string + " | " + deposit + " | " + sc);
        } else {
            throw new SwordAuthException("Você não tem permissão para criar documentos!");
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * checks the AuthCredentials of the user
     *
     * @param auth
     * @return
     * @throws SwordAuthException when user can not authenticate
     */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());

        User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        return (user != null && user.hasPermission(User.CREATE_DOC));
    }
}
