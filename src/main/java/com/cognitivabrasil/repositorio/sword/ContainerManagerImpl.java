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
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import java.util.Map;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.swordapp.server.Deposit;
import spring.ApplicationContextProvider;

public class ContainerManagerImpl implements ContainerManager {

    private static final Logger log = Logger.getLogger(ContainerManagerImpl.class);
    
    /** check authentication */
    private boolean checkAuthCredentials(AuthCredentials auth) throws SwordAuthException {
        log.debug("Dados do sword para autenticação. Usuário: " + auth.getUsername() + " senha: " + auth.getPassword());

        ApplicationContext ctx = ApplicationContextProvider
                  .getApplicationContext();
          UserService userService = ctx.getBean(UserService.class);
          User user = userService.authenticate(auth.getUsername(), auth.getPassword());
          return(user != null && user.hasPermission(User.CREATE_DOC));
    }
 
    @Override
    public DepositReceipt getEntry(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration sc)
    throws SwordServerException, SwordError, SwordAuthException {
        if (checkAuthCredentials(auth)){
            try{
                SwordConfigurationImpl config = (SwordConfigurationImpl) sc;
                String receipt = config.getAlternateUrlContentType();
            } catch (NumberFormatException e) {
                    throw new SwordServerException("Internal Server Error: " + e.getMessage());
            }
        }
  	throw new SwordError("Derivate with ID: " + editIRI + " does not exist", 404);
    }       
    
    @Override
    public DepositReceipt replaceMetadata(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc) 
    throws SwordError, SwordServerException, SwordAuthException {
         throw new UnsupportedOperationException();
    }

    @Override
    public DepositReceipt replaceMetadataAndMediaResource(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
    throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

    @Override
    public DepositReceipt addMetadataAndResources(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
    throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }

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
        if (checkAuthCredentials(auth)){
            try{                
                log.debug("multipart: " + deposit.isMultipart());
                log.debug("binary only: " + deposit.isBinaryOnly());
                log.debug("entry only: " + deposit.isEntryOnly());
                log.debug("in progress: " + deposit.isInProgress());
                log.debug("metadata relevant: " + deposit.isMetadataRelevant());
                log.debug("Salvar o documento: " + editIRI + " | " + deposit + " | " + sc);
            }catch(Exception e){
                throw new SwordServerException("Internal Server Error: " + e.getMessage()); 
            }
        }else{
            throw new SwordAuthException("Você não tem permissão para gravar o aquivo!");
        }
        return null;
    }
   
    @Override
    public void deleteContainer(String editIRI, AuthCredentials auth, SwordConfiguration sc) 
    throws SwordError, SwordServerException, SwordAuthException {
        IRI id = new IRI(editIRI);
        int documentID = -1;
        if (checkAuthCredentials(auth)){
            try{
                if (documentID !=-1){
                    log.debug("Você tentou deletar um documento com id: " + documentID);
                }else{
                    FileUtils.forceDelete(new java.io.File(editIRI));
                    log.debug("Documento com id: " + documentID + "deletado");
                }
            }catch(Exception e){
                throw new SwordServerException("Internal Server Error: " + e.getMessage()); 
            }
        }else{
            throw new SwordAuthException("Você não tem permissão para deletar documentos!");
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt useHeaders(String editIRI, Deposit deposit, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        return null;
    }
    
    @Override
    public boolean isStatementRequest(String editIRI, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config) 
    throws SwordError, SwordServerException, SwordAuthException {
        //TODO check accept Map for content-type that we support for statements
        return false;
    }
}
