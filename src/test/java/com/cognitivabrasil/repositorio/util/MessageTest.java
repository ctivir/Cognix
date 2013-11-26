/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitivabrasil.repositorio.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Test;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class MessageTest {
    private final String MSG = "Mensagem de erro!";
    private final String HREF = "www.marcos.nunes";
    
    @Test
    public void testContrutor(){
        Message msg = new Message(Message.ERROR, MSG, HREF);
        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), equalTo(HREF));        
    }
    
    @Test
    public void testContrutor2(){
        Message msg = new Message(Message.ERROR, MSG);
        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), nullValue());        
    }
    
    @Test
    public void testSetter(){
        Message msg = new Message();
        msg.setType(Message.ERROR);
        msg.setMessage(MSG);
        msg.setHref(HREF);
        
        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), equalTo(HREF)); 
    }
    
}
