package org.juel.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_credentials", schema = "juel")
public class UserCredential {

    @Id
    @Column(name = "email")
    private String userMail;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;


    public String getUserMail() {
        return userMail;
    }

    public String getPassword() {
        return password;
    }


    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
