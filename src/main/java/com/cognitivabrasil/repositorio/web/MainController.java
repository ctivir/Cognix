package com.cognitivabrasil.repositorio.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller geral do Repositorio
 *
 * @author Cognitiva Brasil
 * @author Paulo Schreiner <paulo@jorjao81.com>
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@Controller("main")
public final class MainController {

    private final Logger log = Logger.getLogger(MainController.class);

    public MainController() {
        log.debug("Loaded MainController");
    }

    @RequestMapping("/")
    public String inicio(Model model) {
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
