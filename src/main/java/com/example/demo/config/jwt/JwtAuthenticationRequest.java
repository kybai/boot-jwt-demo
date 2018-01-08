package com.example.demo.config.jwt;

import java.io.Serializable;

/**
 * @author Create by ky.bai on 2018-01-08 10:47
 */
public class JwtAuthenticationRequest implements Serializable {

    private static final long serialVersionUID = -5215461200967217178L;

    private String username;
    private String password;

    public JwtAuthenticationRequest() {
    }

    public JwtAuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
