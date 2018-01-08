package com.example.demo.config.jwt;

import java.io.Serializable;

/**
 * @author Create by ky.bai on 2018-01-08 10:55
 */
public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = -7532692838955009992L;

    private String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
