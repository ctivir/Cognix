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
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.swordapp.server.SwordConfiguration;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class SwordConfigurationImpl implements SwordConfiguration {

    private static final Logger log = Logger.getLogger(SwordConfigurationImpl.class);

    Properties props;

    public SwordConfigurationImpl() {
        Resource resource = new ClassPathResource("/config.properties");
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException ioE) {
            log.fatal("Não foi possível abrir o arquivo config.properties.", ioE);
        }

    }

    @Override
    public boolean returnDepositReceipt() {
        return true;
    }

    @Override
    public boolean returnStackTraceInError() {
        return true;
    }

    @Override
    public boolean returnErrorBody() {
        return true;
    }

    @Override
    public String generator() {
        return "http://www.swordapp.org/";
    }

    @Override
    public String generatorVersion() {
        return props.getProperty("sword.generator.version");
    }

    @Override
    public String administratorEmail() {
        return props.getProperty("administrator.email");
    }

    @Override
    public String getAuthType() {
        return props.getProperty("sword.auth.method");
    }

    @Override
    public boolean storeAndCheckBinary() {
        return true;
    }

    @Override
    public String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    @Override
    public int getMaxUploadSize() {
        return Integer.parseInt(props.getProperty("sword.max.uploaded.file.size", "-1"));
    }

    @Override
    public String getAlternateUrl() {
        return null;
    }

    @Override
    public String getAlternateUrlContentType() {
        return props.getProperty("sword.error.alternate.content-type");
    }

    @Override
    public boolean allowUnauthenticatedMediaAccess() {
        return Boolean.parseBoolean(props.getProperty("sword.allow.unauthenticated.media", "false"));
    }

}
