/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
                + "/documents/");
    }
    
}
