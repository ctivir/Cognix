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

import cognitivabrasil.obaa.General.General;
import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Rights.Rights;
import cognitivabrasil.obaa.Technical.Technical;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
import java.util.List;
import java.util.Map;
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

    private final DocumentService docService;

    private final FileService fileService;

    public CollectionDepositManagerImpl() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        userService = ctx.getBean(UserService.class);
        docService = ctx.getBean(DocumentService.class);
        fileService = ctx.getBean(FileService.class);
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
            Document d = new Document();
            Map<String, List<String>> dc = deposit.getSwordEntry().getDublinCore();
            OBAA obaa = fromDublinCore(dc);
            d.setMetadata(obaa);
            

        } else {
            throw new SwordAuthException("Você não tem permissão para criar documentos!");
        }
        return null;
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

    private OBAA fromDublinCore(Map<String, List<String>> dc) {
        OBAA obaa = new OBAA();
        General g = new General();      
        for (String s:dc.get("dc.description"))
            g.addDescription(s);
        for (String s:dc.get("dc.subject"))
            g.addKeyword(s);
        for (String s:dc.get("dc.title"))
            g.addTitle(s);
        for (String s:dc.get("dc.language"))
            g.addLanguage(s);
        for (String s:dc.get("dc.identifier"))
            g.addIdentifier(new Identifier("", s));
        obaa.setGeneral(g);
        Technical t = new Technical();
        for (String s:dc.get("dc.type"))
            t.addFormat(s);
        obaa.setTechnical(t);
        Rights r = new Rights();
        for (String s:dc.get("dc.rights"))
            r.setDescription(s);
        obaa.setRights(r);
        return obaa;
    }
    
}
