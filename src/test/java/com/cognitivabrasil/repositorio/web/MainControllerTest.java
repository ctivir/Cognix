/*
 * *******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************
 *
 */
package com.cognitivabrasil.repositorio.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Test;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class MainControllerTest {
    
    @Test
    public void testMain(){
        MainController controller = new MainController();
        String result = controller.inicio();
        
        assertThat(result, equalTo("redirect:/documents"));
    }
    
    @Test
    public void testLogin(){
        MainController controller = new MainController();
        String result = controller.logando();
        
        assertThat(result, equalTo("login"));
    }
}
