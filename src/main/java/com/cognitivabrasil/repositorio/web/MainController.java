/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
 
package com.cognitivabrasil.repositorio.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller geral do Repositorio
 *
 * @author Cognitiva Brasil
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@Controller("main")
public final class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class);

    public MainController() {
        LOG.debug("Loaded MainController");
    }

    @RequestMapping("/")
    public String inicio() {
        return "redirect:/documents";
    }

    /**
     * Método para realizar o login.
     *
     * @return manda para pagina de login
     */
    @RequestMapping("/login")
    public String logando() {
        return "login";

    }
}
