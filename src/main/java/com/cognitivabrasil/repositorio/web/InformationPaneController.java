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
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @RequestMapping(method = RequestMethod.GET)
    public String showInformations(Model model){
        List<Informations> info = new ArrayList<>();
        info.add(new Informations("Número de documentos", Long.toString(docService.count())));
        info.add(new Informations("Domínio", "http://cogniticvabrasil.com.br"));
        model.addAttribute("info", info);
        return "panel/show";
    }
}
