package com.sky.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable {

    private String username;
    private String password;
    private String show;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
