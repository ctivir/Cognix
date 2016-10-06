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
import java.util.Arrays;
import java.util.List;
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

    /**
     *
     */
    public SwordConfigurationImpl() {
        Resource resource = new ClassPathResource("/config.properties");
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException ioE) {
            log.fatal("Não foi possível abrir o arquivo config.properties.", ioE);
        }
    }

    /**
     *
     * @return
     */
    public String getBaseUrlPathCurrent() {
        return getBaseUrlPath();
    }

     List<String> getBaseUrlPathsValid() {
        return Arrays.asList(getBaseUrlPath());
    }

    List<String> getBaseUrlPathsDeprecated() {
        return Arrays.asList(getBaseUrlPath());
    }

    String getBaseUrlPath() {
        return "/repositorio/sword";
    }

    /**
     *
     * @return
     */
    @Override
    public boolean returnDepositReceipt() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean returnStackTraceInError() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean returnErrorBody() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String generator() {
        return "http://www.swordapp.org/";
    }

    /**
     *
     * @return
     */
    @Override
    public String generatorVersion() {
        return props.getProperty("sword.generator.version");
    }

    /**
     *
     * @return
     */
    @Override
    public String administratorEmail() {
        return props.getProperty("administrator.email");
    }

    /**
     *
     * @return
     */
    @Override
    public String getAuthType() {
        return props.getProperty("sword.auth.method");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean storeAndCheckBinary() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     *
     * @return
     */
    @Override
    public int getMaxUploadSize() {
        return Integer.parseInt(props.getProperty("sword.max.uploaded.file.size", "-1"));
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlternateUrl() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlternateUrlContentType() {
        return props.getProperty("sword.error.alternate.content-type");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean allowUnauthenticatedMediaAccess() {
        return Boolean.parseBoolean(props.getProperty("sword.allow.unauthenticated.media", "false"));
    }

}