/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import com.cognitivabrasil.repositorio.data.entities.Informations;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.util.Config;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class InformationPanelController {

    @Autowired
    private DocumentService docService;
    @Autowired
    @Qualifier("serverConfig")
    private Properties config;
    @Autowired
    private DataSource dataSource;
    
    private static final Logger log = Logger.getLogger(InformationPanelController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String showInformations(Model model) {
        List<Informations> info = new ArrayList<>();
        info.add(new Informations("Versão", "implementar!"));
        info.add(new Informations("Número de documentos", Long.toString(docService.count())));
        
        info.add(new Informations("Domínio", config.getProperty("Repositorio.hostname"), "Este dominio será utilizado para criar a localização dos documentos. Editar no arquivo: 'config.properties'."));
        info.add(new Informations("Raiz do projeto", config.getProperty("Repositorio.rootPath", "/repositorio")));
        info.add(new Informations("Porta", config.getProperty("Repositorio.port", "8080"),"Porta informada no arquivo '/WEB-INF/classes/config.properties'"));
        info.add(new Informations("URL dos objetos", Config.getUrl(config)+"{id}", "URL que será utilizada para criar o location dos objetos. Pode ser editada em: '/WEB-INF/classes/config.properties'"));
        try{
        DatabaseMetaData databaseInfo = dataSource.getConnection().getMetaData();
        info.add(new Informations("Base de dados utilizada", databaseInfo.getDatabaseProductName()));
        info.add(new Informations("Versão da base de dados", databaseInfo.getDatabaseProductVersion()));
        info.add(new Informations("JDBC driver", databaseInfo.getDriverName()));
        info.add(new Informations("Versão do JDBC driver", databaseInfo.getDriverVersion()));
        info.add(new Informations("URL da base de dados", databaseInfo.getURL()));
        info.add(new Informations("Usuário da base de dados", databaseInfo.getUserName()));
               
        }catch(SQLException s){
            log.error("Error getting information about database.");
            info.add(new Informations("Erro","Não foi possível carregar os dados da base de dados"));
        }
        model.addAttribute("info", info);
        return "panel/show";
    }
}
