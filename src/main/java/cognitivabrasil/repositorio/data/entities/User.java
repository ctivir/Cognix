package cognitivabrasil.repositorio.data.entities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import spring.ApplicationContextProvider;

public class User implements UserDetails {

    public static final String MANAGE_DOC = "PERM_MANAGE_DOC";
    public static final String VIEW = "PERM_VIEW";
    public static final String MANAGE_USER = "PERM_MANAGE_USERS";
    public static final String CREATE_DOC = "PERM_CREATE_DOC";
    private Integer id;
    private String login;
    private String passwordMd5;
    private String nome;
    /* Internal representation of permission, as a string separated bu commas */
    private String permissionsInternal;
    private String role;
    private static final Map<String, String> ROLES;

    static {
        SortedMap<String, String> myRoles = new TreeMap<String, String>();
        myRoles.put("admin", "Administrador de documentos");
        myRoles.put("author", "Criador de documentos");
        myRoles.put("view", "Somente visualizar");
        myRoles.put("root", "Superusu\u00e1rio");
        ROLES = Collections.unmodifiableSortedMap(myRoles);
    }

    public User() {
        login = "";
        passwordMd5 = "";
        nome = "";
        permissionsInternal = "";
        role = "";
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
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
    public String getNome() {
        return nome;
    }

    /**
     * Sets the User Name
     *
     * @param nome User name
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the passwordMd5
     */
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
        if (password == null) {
            return false;
        }
        return getPasswordMd5().equals(getPasswordEncoder().encodePassword(password, null));
    }

    /*
     * Implemented to satisfy SpringSecurity
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities =
                new HashSet<GrantedAuthority>();
        for (String s : getPermissions()) {
            authorities.add(new GrantedAuthorityImpl(s));
        }
        return authorities;

    }

    @Override
    public String getPassword() {
        return getPasswordMd5();
    }

    @Override
    public String getUsername() {
        return login;

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @return the passwordEncoder
     */
    // TODO: Use autowired here, probalby needs AOP
    private PasswordEncoder getPasswordEncoder() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        return ctx.getBean(PasswordEncoder.class);
    }

    /**
     * @return the permissions
     */
    public Set<String> getPermissions() {
        return new HashSet<String>(Arrays.asList(StringUtils.split(getPermissionsInternal(), ',')));
    }

    /**
     * @param perm the permissions to set
     */
    private void setPermissions(Set<String> perm) {
        setPermissionsInternal(StringUtils.join(perm, ','));
    }

    /**
     * @return the permissionsInternal
     */
    private String getPermissionsInternal() {
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
    @Transient
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
                && u.nome.equals(nome)
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
        hash = 11 * hash + (this.nome != null ? this.nome.hashCode() : 0);
        hash = 11 * hash + (this.permissionsInternal != null ? this.permissionsInternal.hashCode() : 0);
        hash = 11 * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }

    /**
     * Gets the permissions by the role.
     *
     * @param role the role
     * @return the permissions
     */
    private String getPermissions(String role) {
        Map<String, String> roles = new HashMap<String, String>();
        roles.put(
                "root",
                User.MANAGE_USER + "," + User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put(
                "admin",
                User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put("author",
                User.CREATE_DOC);
        roles.put("view",
                User.VIEW);
        return roles.get(role);

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
     * @param role String role
     * @return
     */
    public String getRoleNameText() {
        return User.getRoles().get(role);
    }
}
