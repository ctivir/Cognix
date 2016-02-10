/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package metadata.conversor;

import com.cognitivabrasil.repositorio.web.MainController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 *
 * @author paulo
 *
 * Classe para criar regras de conversão.
 */
public class Rule {

    private String from;
    private String to;
    private static final Logger LOG = Logger.getLogger(Rule.class);

    /**
     *
     * @param from
     * @param to
     */
    Rule(String f, String t) {
        from = f;
        to = t;
        
    }

    /**
     *
     * @param objeto1 Objeto origem da transformacao. Neste objeto será chamada
     * a função "get" + from
     * @param objeto2 Objeto destino. Neste objeto será chamado o método "set" +
     * from, com o resultado do get do objeto 1
     * @throws IllegalArgumentException Caso um dos métodos não exista.
     */
    public void apply(Object ob1, Object ob2) throws IllegalArgumentException {
        Class c1 = ob1.getClass();
        Class c2 = ob2.getClass();
        Method m1, m2;
        
        try {            
            m1 = c1.getMethod("get" + from);
        } catch (NoSuchMethodException e) {            
            throw new IllegalArgumentException("Cannot find method " + "get" + from
                    + " in class" + c1.getName(), e);
        }
        
        try {
            m2 = c2.getMethod("set" + to, m1.getReturnType());
        } catch (NoSuchMethodException e) {            
            throw new IllegalArgumentException("Cannot find method " + "set" + from
                    + " in class" + c2.getName(), e);
        }
        
        
        try {
            m2.invoke(ob2, m1.invoke(ob1));
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            LOG.error(e);            
        }
    }
}
