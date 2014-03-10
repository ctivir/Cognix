/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cognitivabrasil.repositorio.data.entities;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class Informations {
    private String title;
    private String value;
    private String msg;

    public Informations(String title, String value, String msg) {
        this.title = title;
        this.value = value;
        this.msg = msg;
    }

    public Informations(String title, String value) {
        this.title = title;
        this.value = value;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
