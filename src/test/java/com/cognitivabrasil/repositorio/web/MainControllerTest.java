/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
