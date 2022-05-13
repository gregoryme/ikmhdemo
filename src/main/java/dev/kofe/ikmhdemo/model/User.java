package dev.kofe.ikmhdemo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //
    // ATTENTION: we will use email as a username (login)
    //
    private String username; // email

    @Transient
    private String usernameConfirm;

    public String getUsernameConfirm() { return usernameConfirm; }

    public void setUsernameConfirm(String usernameConfirm) {
        this.usernameConfirm = usernameConfirm;
    }

    @Size(min = 5,   message = "Password should be no less than 5 symbols")
    @Size(max = 100, message = "Password should be no more than 100 symbols")
    private String password;

    @Transient
    private String passwordConfirm;

    private boolean confirmed = false;
    public boolean getConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

    private AcademyMember academyMember = AcademyMember.STUDENT;

    public AcademyMember getAcademyMember() { return academyMember; }
    public void setAcademyMember(AcademyMember academyMember) {
        this.academyMember = academyMember;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
