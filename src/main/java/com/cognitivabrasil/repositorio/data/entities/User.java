package com.cognitivabrasil.repositorio.data.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spring.ApplicationContextProvider;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    public static final String MANAGE_DOC = "PERM_MANAGE_DOC";
    public static final String VIEW = "PERM_VIEW";
    public static final String MANAGE_USER = "PERM_MANAGE_USERS";
    public static final String CREATE_DOC = "PERM_CREATE_DOC";
    public static final String ROLE_DOC_ADMIN = "docadmin";
    public static final String ROLE_AUTHOR = "author";
    public static final String ROLE_VIEW = "view";
    public static final String ROLE_ROOT = "root";
    
    private static PasswordEncoder passEncoder;
    private Integer id;
    private String login;
    private String passwordMd5;
    private String name;
    /* Internal representation of permission, as a string separated bu commas */
    private String permissionsInternal;
    private String role;
    private static final Map<String, String> ROLES;
    private Boolean deleted;

    static {
        SortedMap<String, String> myRoles = new TreeMap<>();
        myRoles.put(ROLE_DOC_ADMIN, "Administrador de documentos");
        myRoles.put(ROLE_AUTHOR, "Criador de documentos");
        myRoles.put(ROLE_VIEW, "Somente visualizar");
        myRoles.put(ROLE_ROOT, "Superusu\u00e1rio");
        ROLES = Collections.unmodifiableSortedMap(myRoles);
    }

    public User() {
        login = "";
        passwordMd5 = "";
        name = "";
        permissionsInternal = "";
        role = "";
        deleted = false;
    }
    
    /**
     * @return the passwordEncoder
     */
    // TODO: Use autowired here, probalby needs AOP
    private static PasswordEncoder getPasswordEncoder() {
        if (passEncoder == null) {
            ApplicationContext ac = ApplicationContextProvider.getApplicationContext();
            passEncoder = ac.getBean(PasswordEncoder.class);
        }
        return passEncoder;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Override
    @Column(name="login")
    public String getUsername() {
        return login;
    }

    /**
     * Sets the login.
     *
     * @param login the new login
     */
    public void setUsername(String login) {
        this.login = login;
    }

    /**
     *
     * @return User name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the User Name
     *
     * @param nome User name
     */
    public void setName(String nome) {
        this.name = nome;
    }

    /**
     * @return the passwordMd5
     */
    @Column(name = "password")
    protected String getPasswordMd5() {
        return passwordMd5;
    }

    /**
     * @param passwordMd5 the passwordMd5 to set
     */
    protected void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    /**
     * Sets the user password
     *
     * @param password New password
     */
    public void setPassword(String password) {
        assert (getPasswordEncoder() != null);
        setPasswordMd5(getPasswordEncoder().encodePassword(password, null));
    }

    /**
     * Authenticates this user
     *
     * @param password Cleartext password
     * @return true if password matches the user, false otherwise
     * @deprecated authentication should be done by spring security
     */
    @Deprecated
    public boolean authenticate(String password) {
        if (password == null || getPasswordMd5() == null) {
            return false;
        }
        return getPasswordMd5().equals(getPasswordEncoder().encodePassword(password, null));
    }

    @Override
    @Transient
    public String getPassword() {
        return getPasswordMd5();
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return !isDeleted();
    }

    @Override
    @Transient
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return !isDeleted();
    }

    /**
     * @return the permissions
     */
    @Transient
    public Set<String> getPermissions() {
        return new HashSet<>(Arrays.asList(StringUtils.split(getPermissionsInternal(), ',')));
    }


    /**
     * @return the permissionsInternal
     */
    @Column(name = "permissions")
    protected String getPermissionsInternal() {
        return permissionsInternal;
    }

    /**
     * @param permissionsInternal the permissionsInternal to set
     */
    private void setPermissionsInternal(String permissionsInternal) {
        this.permissionsInternal = permissionsInternal;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
        setPermissionsInternal(getPermissions(role));
    }
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the permissions by the role.
     *
     * @param role the role
     * @return the permissions
     */
    private String getPermissions(String role) {
        Map<String, String> roles = new HashMap<>();
        roles.put(
                ROLE_ROOT,
                User.MANAGE_USER + "," + User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put(
                ROLE_DOC_ADMIN,
                User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put(ROLE_AUTHOR,
                User.CREATE_DOC);
        roles.put(ROLE_VIEW,
                User.VIEW);
        return roles.get(role);

    }
    
    /**
     * Checks if the user is root.
     * @return Return true if the user is root and false otherwise
     */
    @Transient
    public boolean isRoot(){
        return role.equalsIgnoreCase(ROLE_ROOT);
    }

    /**
     * Reference data for the roles.
     *
     * @return the map
     */
    public static Map<String, String> getRoles() {
        return ROLES;
    }

    /**
     * Gets the name of role
     *
     * @return
     */
    @Transient
    public String getRoleNameText() {
        return User.getRoles().get(role);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User u = (User) o;
        return u.login.equals(login) && u.passwordMd5.equals(passwordMd5)
                && u.name.equals(name)
                && u.id.equals(id)
                && u.permissionsInternal.equals(permissionsInternal)
                && u.role.equals(role);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 11 * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = 11 * hash + (this.passwordMd5 != null ? this.passwordMd5.hashCode() : 0);
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + (this.permissionsInternal != null ? this.permissionsInternal.hashCode() : 0);
        hash = 11 * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }
    
    /*
     * Implemented to satisfy SpringSecurity
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities =
                new HashSet<>();
        for (String s : getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(s));
        }
        return authorities;
    }
}
