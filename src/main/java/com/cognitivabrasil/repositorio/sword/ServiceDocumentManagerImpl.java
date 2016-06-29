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

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ServiceDocument;
import org.swordapp.server.ServiceDocumentManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.SwordWorkspace;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Component
public class ServiceDocumentManagerImpl implements ServiceDocumentManager {

    @Override
    public ServiceDocument getServiceDocument(String string, AuthCredentials ac, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        Resource resource = new ClassPathResource("/config.properties");
        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException io) {
            throw new SwordServerException("Erro ao carregar o arquivo properties", io);
        }

        ServiceDocument document = new ServiceDocument();

        document.setVersion(props.getProperty("sword.generator.version"));
        document.setMaxUploadSize(Integer.parseInt(props.getProperty("sword.max.uploaded.file.size", "-1")));
        SwordWorkspace workspace = new SwordWorkspace();
        workspace.setTitle(props.getProperty("Repositorio.name"));

//        SwordCollection collection = new SwordCollection();
//        collection.setTitle(MCRConfiguration.instance().getString("MCR.SWORD.default.collection.title"));
//        collection.addAcceptPackaging(METS_PACKAGING);
//        collection.addAccepts(ZIP_MIME_TYPE);
//        collection.setMediation(false);
//        collection.setCollectionPolicy(MCRConfiguration.instance().getString("MCR.SWORD.default.collection.policy"));
//        collection.setTreatment(MCRConfiguration.instance().getString("MCR.SWORD.default.collection.treatment"));
//        collection.setAbstract(MCRConfiguration.instance().getString("MCR.SWORD.default.collection.abstract"));
//        collection.setLocation(MCRServlet.getBaseURL() + MCRConfiguration.instance().getString("MIL.SWORD.collection.IRI"));
//        workspace.addCollection(collection);
        document.addWorkspace(workspace);

        return document;
    }

}
