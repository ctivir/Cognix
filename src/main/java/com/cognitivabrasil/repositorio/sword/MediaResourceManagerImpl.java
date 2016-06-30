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

public class MediaResourceManagerImpl implements MediaResourceManager {

    private static final Logger log = Logger.getLogger(MediaResourceManagerImpl.class);

    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        //ApplicationContext ctx = null;
        //UserService userService = ctx.getBean(UserService.class);
        //User user = userService.authenticate(auth.getUsername(), auth.getPassword());
        //log.debug("getEntry called with url: " + uri);
        return null;
        
    }
   
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException 
    {
	throw new SwordServerException("nenhuma implementação ainda MediaResourceManager");
    }
    
    public void deleteMediaResource(String uri, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        //User user = getUser(auth);
        
 	throw new SwordServerException ();
    }
    
    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException 
    {
        DepositReceipt receipt = new DepositReceipt();
	//TODO
	receipt.setLocation(new IRI("http://example.com"));
	return receipt;
    }

}
