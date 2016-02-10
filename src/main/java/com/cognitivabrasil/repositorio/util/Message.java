/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
 
package com.cognitivabrasil.repositorio.util;

public class Message {

    private String type;
    private String message;
    private String href;
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String WARN = "warn";

    public Message() {
    }

    /**
     * Cria uma mensagem que será passada para a pagina seguinte
     * @param type tipo de mensagem. Normalmente é o nome de um class css. Ex: "sucess" ou "error"
     * @param message texto que será exibido
     */
    public Message(String type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Cria uma mensagem que será passada para a pagina seguinte, normalmente utilizado com ajax.
     * Neste construtor se passa a mensagem, o tipo de mensagem (sucess ou error)
     * e um href que será utilizado no jquery para redirecionar a página.
     * será aberto em um dialog ou não.
     * @param type tipo de mensagem. Normalmente é o nome de um class css. Ex: "sucess" ou "error"
     * @param message texto que será exibido
     * @param href link para onde o usuário será redirecionado
     */
    public Message(String type, String message, String href) {
        this.type = type;
        this.message = message;
        this.href = href;
        
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}