/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.Informations;
import com.cognitivabrasil.repositorio.services.DocumentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RequestMapping("/panel")
@Controller
public class InformationPaneController {

    @Autowired
    private DocumentService docService;
    @Autowired
    @Qualifier("serverConfig")
    private Properties config;
    @Autowired
    private BasicDataSource dataSource;

    @RequestMapping(method = RequestMethod.GET)
    public String showInformations(Model model) {
        List<Informations> info = new ArrayList<>();
        info.add(new Informations("Versão", "implementar!"));
        info.add(new Informations("Número de documentos", Long.toString(docService.count())));
        
        info.add(new Informations("Domínio", config.getProperty("Repositorio.hostname"), "Este dominio será utilizado para criar a localização dos documentos. É muito imporante que esteja correto."));
        info.add(new Informations("Raiz do projeto", config.getProperty("Repositorio.rootPath", "/repositorio")));
        info.add(new Informations("Porta", config.getProperty("Repositorio.port", "8080")));
        info.add(new Informations("JDBC Driver", dataSource.getDriverClassName()));
        info.add(new Informations("Base de dados", dataSource.getUrl()));
        info.add(new Informations("Usuário da base de dados", dataSource.getUsername()));
        model.addAttribute("info", info);
        return "panel/show";
    }
}
