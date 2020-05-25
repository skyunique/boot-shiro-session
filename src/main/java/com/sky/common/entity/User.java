package com.sky.common.entity;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String show;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
