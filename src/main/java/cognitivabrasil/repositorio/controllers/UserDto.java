/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.controllers;

import cognitivabrasil.repositorio.data.entities.User;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * @author marcos
 */
public class UserDto {
    
    private String username;
    private String nome;
    private String role;
    private String password;
    private String confirmPass;
    private String currentPass;
    private int id;

    public UserDto() {
    }

    public UserDto(String username, String nome, String password, int id, String role) {
        this.username = username;
        this.nome = nome;
        this.password = password;
        this.id = id;
        this.role = role;
    }

    public UserDto(User u) {
        this.username = u.getUsername();
        this.nome = u.getNome();
        this.password = u.getPassword();
        this.id = u.getId();
        this.role = u.getRole();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.replaceAll("\\+", " ");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    /**
     * Used only on editing user, to confirm the password with the database
     *
     * @return The current password set in the edit form
     */
    public String getCurrentPass() {
        return currentPass;
    }

    /**
     * Used only on editing user, to confirm the password with the database
     *
     * @param currentPass The current password
     */
    public void setCurrentPass(String currentPass) {
        this.currentPass = currentPass;
    }

    public User updateUser(User u) {
        if (u == null) {
            u = new User();
        }
        u.setNome(nome);
        u.setUsername(username);
        u.setRole(role);
        if (!isBlank(password)) {
            u.setPassword(password);
        }
        return u;
    }

    public User getUser() {
        User u = new User();
        return updateUser(u);
    }

}
