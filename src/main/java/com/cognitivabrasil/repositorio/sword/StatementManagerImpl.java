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
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Statement;
import org.swordapp.server.StatementManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import spring.ApplicationContextProvider;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 *
 *  This class contains the method signatures for retrieving Statement objects from the server 
 *  The Statement is a document which describes two features of the object as it appears on the server:
 *  1.Structure. This may include originally uploaded content files, unpackaged content, derived content 
 *  and any other features of the object
 *  2.State. This allows the server to indicate to the client some information with regard to the state of 
 *  the item on the server, including but not limited, to its ingest workflow position.
 */
public class StatementManagerImpl implements StatementManager {
    
    /** Logger */
    private static final Logger log = Logger.getLogger(StatementManagerImpl.class);
    
    /** check authentication */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        UserService userService = ctx.getBean(UserService.class);
        User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        return (user != null && user.hasPermission(User.CREATE_DOC));
    }
    
    /**
     *
     * @param iri
     * @param accept
     * @param auth
     * @param config
     * @return
     * @throws SwordServerException
     * @throws SwordError
     * @throws SwordAuthException
     */
    @Override 
    public Statement getStatement(String iri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
    throws SwordServerException, SwordError, SwordAuthException {
        if (checkAuthCredentials(auth)) {
            Statement statement = new Statement() {
            @Override            
                public void writeTo(Writer out) throws IOException {
                    out.write("Deposito aprovado..."); 
                }
            };
            try {
                statement.getLastModified();
            }catch (Exception e){
                throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
            Map<String, String> map = Collections.singletonMap("StateDescription", "O item passou pelo fluxo de trabalho ...");
            statement.setStates(map);
            
            //List<OriginalDeposit> list = Collections.singletonList(originalDeposit);
            //statement.setOriginalDeposits(list);
 	
            return statement;
 	} else {
            throw new SwordError("Document with ID: " + (iri) + " does not exist");
        }   
    }
}
