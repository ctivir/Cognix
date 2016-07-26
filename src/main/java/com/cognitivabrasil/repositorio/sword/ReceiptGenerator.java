/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.sword;

import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.Document;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.log4j.Logger;
import org.swordapp.server.DepositReceipt;

/**
 *
 * @author Cecilia
 */
public class ReceiptGenerator {

    private static final Logger log = Logger.getLogger(ReceiptGenerator.class);

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
