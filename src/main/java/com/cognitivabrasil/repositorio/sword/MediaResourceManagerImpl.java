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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.swordapp.server.UriRegistry;

public class MediaResourceManagerImpl implements MediaResourceManager {

    private static final Logger log = Logger.getLogger(MediaResourceManagerImpl.class);

    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        log.info("getMediaResourceRepresentation: " + uri);
        IRI mediaUri = new IRI(uri);
        
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
    
        throw new SwordServerException ();
    }
    
    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials auth, SwordConfiguration sc) 
    throws SwordError, SwordServerException, SwordAuthException 
    {
        DepositReceipt receipt = new DepositReceipt();
	//TODO
	receipt.setLocation(new IRI("http://example.com"));
	return receipt;
    }
}
