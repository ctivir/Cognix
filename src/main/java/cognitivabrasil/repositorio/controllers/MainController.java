package cognitivabrasil.repositorio.controllers;

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

    Logger log = Logger.getLogger(MainController.class);

    public MainController() {
        log.info("Loaded MainController");
    }

    @RequestMapping("/")
    public String inicio(Model model) {
        return "redirect:/documents";
    }

    /**
     * Método para realizar o login.
     *
     * @param login Passado por HTTP
     * @param password Passado por HTTP
     * @return Redirect para adm caso autentique, permanece nesta página com uma
     * mensagem de erro caso contrário
     */
    @RequestMapping("/login")
    public String logando() {
        return "login";

    }
}
