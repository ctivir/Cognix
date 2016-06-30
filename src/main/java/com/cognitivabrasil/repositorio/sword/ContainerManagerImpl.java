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

import org.apache.log4j.Logger;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import java.util.Map;

public class ContainerManagerImpl implements ContainerManager {

    private static final Logger log = Logger.getLogger(ContainerManagerImpl.class);

    public DepositReceipt getEntry(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
    throws SwordServerException, SwordError, SwordAuthException {
        return null; 
    }       
    
    public DepositReceipt replaceMetadata(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException();
    }

    public DepositReceipt replaceMetadataAndMediaResource(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
    throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DepositReceipt addMetadataAndResources(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config)
    throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DepositReceipt addMetadata(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DepositReceipt addResources(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

    public void deleteContainer(String editIRI, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        
        throw new UnsupportedOperationException();
    }

    public DepositReceipt useHeaders(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException();
    }
    
    public boolean isStatementRequest(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        //TODO check accept Map for content-type that we support for statements
        return false;
    }

    protected static DepositReceipt getDepositReceipt(String pid, AuthCredentials auth)
    throws SwordServerException {    
        return null;
    }
}
