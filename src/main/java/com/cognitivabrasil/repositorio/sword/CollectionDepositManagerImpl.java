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
import cognitivabrasil.obaa.LifeCycle.Contribute;
import cognitivabrasil.obaa.LifeCycle.LifeCycle;
import cognitivabrasil.obaa.Metametadata.Role;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Rights.Rights;
import cognitivabrasil.obaa.Technical.Technical;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.FileService;
import com.cognitivabrasil.repositorio.util.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Link;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
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
    
    public final String filesPath = "./src/test/resources/files/";
    
    private Properties properties;
    
    UrlManager urlManager;
            
    public CollectionDepositManagerImpl() {
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

    /**
     *
     * @param collectionUri
     * @param deposit
     * @param auth
     * @param sc
     * @return
     * @throws SwordError
     * @throws SwordServerException
     * @throws SwordAuthException
     */
    @Override
    public DepositReceipt createNew(String collectionUri, Deposit deposit, AuthCredentials auth, SwordConfiguration sc)
            throws SwordError, SwordServerException, SwordAuthException {
        if (checkAuthCredentials(auth)) {
            Document d = new Document();
            OBAA obaa = fromDublinCore(deposit.getSwordEntry().getDublinCore());
            d.setMetadata(obaa);

            File f = deposit.getFile();
            File createdFile;
            Files file = null;
            try {
                createdFile = saveFile(f);
                file = new Files();
                file.setLocation(createdFile.getPath());
                file.setName(createdFile.getName());
                fileService.save(file);
                d.setFiles(Arrays.asList(file));
                docService.save(d);
            } catch (IOException ex) {
                log.error("Erro ao salvar arquivo",ex);
                throw new SwordServerException();
            }
            DepositReceipt dr = new DepositReceipt();
            Link l = deposit.getSwordEntry().getEntry().getEditLink();
            dr.setOriginalDeposit((l==null)? "" :l.toString(), "");
            dr.setEditIRI(urlManager.getEditIRI(file));
            dr.setEditMediaIRI(urlManager.getEmIRI(file));
            dr.setSwordEditIRI(urlManager.getEditIRI(file));
            dr.setLocation(urlManager.getEditIRI(file));
            //Todo: Edit-IRI, EM-IRI, SE-IRI, Treatment -> MUST
            // originaldeposit -> SHOULD
            return dr;

        } else {
            throw new SwordAuthException("Você não tem permissão para criar documentos!");
        }
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

    /**
     * Mapeia os metadados em Dublin Core para o formato OBAA
     *
     * TODO: dc.description.abstract == general.description e dc.contributor.creator no
     * LifeCycle.Contribute.Entity
     *
     * @param dc
     * @return OA no formato OBAA
     */
    private OBAA fromDublinCore(Map<String, List<String>> dc) {
        OBAA obaa = new OBAA();
        General g = new General();
        for (String s : safe(dc.get("dc.description"))) {
            g.addDescription(s);
        }
        for (String s : safe(dc.get("dc.subject"))) {
            g.addKeyword(s);
        }
        for (String s : safe(dc.get("dc.title"))) {
            g.addTitle(s);
        }
        for (String s : safe(dc.get("dc.language"))) {
            g.addLanguage(s);
        }
        for (String s : safe(dc.get("dc.identifier"))) {
            g.addIdentifier(new Identifier("", s));
        }
        obaa.setGeneral(g);
        Technical t = new Technical();
        for (String s : safe(dc.get("dc.type"))) {
            t.addFormat(s);
        }
        obaa.setTechnical(t);
        LifeCycle lc = new LifeCycle();
        Contribute c = new Contribute();
        for (String s : safe (dc.get("dc.contributor.creator"))){
            c.addEntity(s);
            c.setRole(Role.CREATOR);
            lc.addContribute(c);
        }
        obaa.setLifeCycle(lc);
        Rights r = new Rights();
        for (String s : safe(dc.get("dc.rights"))) {
            r.setDescription(s);
        }
        obaa.setRights(r);
        return obaa;
    }

    public <T> List<T> safe(List<T> l){
        return l == null ? Collections.EMPTY_LIST : l;
    }
    /**
     * Grava o arquivo
     *
     * @param f
     * @return 
     * @throws IOException
     */
    public File saveFile(File f) throws IOException {       
        InputStream is = new FileInputStream(new File(f.getPath()));
        //Todo: pegar local do arquivo em alguma configuração externa
        String pathToSave = filesPath + f.getName();
        File f1 = new File(pathToSave);
        f1.createNewFile();
        OutputStream os = new FileOutputStream(f1);
        IOUtils.copy(is, os);
        return f1;
    }

}
