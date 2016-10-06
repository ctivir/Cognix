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
import com.cognitivabrasil.repositorio.data.entities.Document;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.log4j.Logger;
import org.swordapp.server.DepositReceipt;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 */
public class ReceiptGenerator {

    private static final Logger log = Logger.getLogger(ReceiptGenerator.class);

    /**
     *
     * @param baseUrl
     * @param files
     * @return
     */
    public DepositReceipt createDatasetReceipt(String baseUrl, Files files) {
        log.debug("baseUrl era: " + baseUrl);
        DepositReceipt depositReceipt = new DepositReceipt();
        String globalId = files.getLocation();
        String editIri = baseUrl + "/edit/" + globalId;
        depositReceipt.setEditIRI(new IRI(editIri));

        depositReceipt.setLocation(new IRI(editIri));
        depositReceipt.setEditMediaIRI(new IRI(baseUrl + "/edit-media/" + globalId));
        depositReceipt.setStatementURI("application/atom+xml;type=feed", baseUrl + "/statement/" + globalId);
        //depositReceipt.addDublinCore("bibliographicCitation", files.getLatestVersion().getCitation());
        //depositReceipt.setSplashUri(files.getPersistentURL());
        return depositReceipt;
    }
    
    /**
     *
     * @param baseUrl
     * @param document
     * @return
     */
    public DepositReceipt createDocumentReceipt(String baseUrl, Document document) {
        log.debug("baseUrl era: " + baseUrl);
        DepositReceipt depositReceipt = new DepositReceipt();
        String globalId = document.getTitle();
        String collectionIri = baseUrl + "/collection/" + globalId;
        depositReceipt.setSplashUri(collectionIri);
        
        String editIri = baseUrl + "/edit/" + globalId;
        depositReceipt.setEditIRI(new IRI(editIri));
        return depositReceipt;
    }
}