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

import com.cognitivabrasil.repositorio.util.Config;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.swordapp.server.SwordError;
import org.swordapp.server.UriRegistry;

/**
 *
 * @author Cecilia Tivir <ctivir@gmail.com>
 */
public class UrlManager {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(UrlManager.class);

    private Properties config;

    String originalUrl;
    SwordConfigurationImpl swordConfiguration = new SwordConfigurationImpl();
    String servlet;
    String targetType;
    String targetIdentifier;
    int port;

    String processUrl(String url) throws SwordError {
        log.debug("URL era: " + url);
        String warning = null;
        this.originalUrl = url;
        URI javaNetUri;
        try {
            javaNetUri = new URI(url);
        } catch (URISyntaxException ex) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Invalid URL syntax: " + url);
        }
        if (!"https".equals(javaNetUri.getScheme())) {
            log.debug("https is required but protocol was " + javaNetUri.getScheme());
        }
        this.port = javaNetUri.getPort();
        String[] urlPartsArray = javaNetUri.getPath().split("/");
        List<String> urlParts = Arrays.asList(urlPartsArray);
        String dataDepositApiBasePath;
        try {
            List<String> dataDepositApiBasePathParts;
            //             1 2          3     4           5
            // for example: /repositorio/sword/collection/sword
            dataDepositApiBasePathParts = urlParts.subList(0, 4);
            dataDepositApiBasePath = StringUtils.join(dataDepositApiBasePathParts, "/");
        } catch (IndexOutOfBoundsException ex) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Error processing URL: " + url);
        }
        if (!swordConfiguration.getBaseUrlPathsValid()
                .contains(dataDepositApiBasePath)) {
            throw new SwordError(dataDepositApiBasePath + " found but one of these required: " + swordConfiguration.getBaseUrlPathsValid() + ". Current version is " + swordConfiguration.getBaseUrlPathCurrent());
        } else if (swordConfiguration.getBaseUrlPathsDeprecated().contains(dataDepositApiBasePath)) {
            String msg = "Deprecated version. The current version expects '" + swordConfiguration.getBaseUrlPathCurrent() + "'. URL passed in: " + url;
            warning = msg;
        }
        try {
            this.servlet = urlParts.get(4);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine servlet path from URL: " + url);
        }
        if (!servlet.equals(
                "service-document")) {
            List<String> targetTypeAndIdentifier;
            try {
                //           4          5
                // for example: /collection/sword
                targetTypeAndIdentifier = urlParts.subList(5, urlParts.size());
            } catch (IndexOutOfBoundsException ex) {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "No target components specified in URL: " + url);
            }
            this.targetType = targetTypeAndIdentifier.get(0);
            if (targetType != null) {
                if (targetType.equals("document")) {
                    String docAlias;
                    try {
                        docAlias = targetTypeAndIdentifier.get(1);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "No document alias provided in URL: " + url);
                    }
                    this.targetIdentifier = docAlias;
                } else if (targetType.equals("file")) {
                    String fileIdString;
                    try {
                        fileIdString = targetTypeAndIdentifier.get(1);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "No file id provided in URL: " + url);
                    }
                    this.targetIdentifier = fileIdString;
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "unsupported target type: " + targetType);
                }
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine target type from URL: " + url);
            }
            log.debug("target type: " + targetType);
            log.debug("target identifier: " + targetIdentifier);
        }
        if (warning
                != null) {
            log.debug(warning);
        }
        return warning;
    }

    String getHostnamePlusBaseUrlPath(String url) throws SwordError {
        String optionalPort = "";
        URI u;
        try {
            u = new URI(url);
        } catch (URISyntaxException ex) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "unable to part URL");
        }
        int port = u.getPort();
        if (port != -1) {
            optionalPort = ":" + port;
        }
        String requestedHostname = u.getHost();
        String hostName = System.getProperty(Config.getUrl(config));
        if (hostName == null) {
            hostName = "localhost";
        }
        if (requestedHostname.equals("localhost")) {
            hostName = "localhost";
        }
        return "https://" + hostName + optionalPort + swordConfiguration.getBaseUrlPathCurrent();
    }

    /**
     *
     * @return
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     *
     * @param originalUrl
     */
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    /**
     *
     * @return
     */
    public String getServlet() {
        return servlet;
    }

    /**
     *
     * @param servlet
     */
    public void setServlet(String servlet) {
        this.servlet = servlet;
    }

    /**
     *
     * @return
     */
    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    /**
     *
     * @param targetIdentifier
     */
    public void setTargetIdentifier(String targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
    }

    /**
     *
     * @return
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     *
     * @param targetType
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }
}
