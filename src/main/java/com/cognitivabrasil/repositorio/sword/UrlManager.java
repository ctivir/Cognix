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
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.util.Config;
import java.io.IOException;
import java.util.Properties;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.swordapp.server.SwordServerException;
import spring.ApplicationContextProvider;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 */
public class UrlManager {
    
    private final UserService userService;

    private final DocumentService docService;

    private final FileService fileService;
    
    public final String filesPath = "./src/test/resources/files/";
    
    private Properties properties;
    
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(UrlManager.class);
    
    public UrlManager() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        userService = ctx.getBean(UserService.class);
        docService = ctx.getBean(DocumentService.class);
        fileService = ctx.getBean(FileService.class);
        Resource resource = new ClassPathResource("/config.properties");
        try {
            properties = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            log.error("Não foi possível carregar o arquivo de configurações", e);
        }
}
 
    public IRI getEditIRI(Files f) throws SwordServerException {
        IRI editIRI = new IRI(Config.getUrl(properties)+"/sword/edit/"+f.getId());
	return editIRI;
        }
    
    public IRI getEmIRI(Files f) {
	        IRI emIRI = new IRI(Config.getUrl(properties)+"/sword/edit-media/"+f.getId());
        return emIRI;
        }
       
    public IRI getStatementIRI(Files f) {
	        IRI stateIRI = new IRI(Config.getUrl(properties)+"/sword/statement/"+f.getId()+".rdf");
        return stateIRI;
        }
}
