/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package com.cognitivabrasil.repositorio.util;

import java.util.Properties;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class Config {
    public static final String FILE_PATH = "/var/cognitiva/repositorio/";
    
    public static String getUrl(Properties config){
        String port = config.getProperty("Repositorio.port", "8080");
        return ("http://"
                + config.getProperty("Repositorio.hostname")
                + (port.equals("80") ? "" : (":" + port))
                // if port 80, dont put anything
                + config.getProperty("Repositorio.rootPath", "/repositorio")
                + "/documents");
    }
    
}
