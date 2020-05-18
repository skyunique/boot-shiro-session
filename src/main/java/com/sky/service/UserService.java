package com.sky.service;

import com.sky.common.entity.User;

import java.util.List;

public interface UserService {

    void addUser(User user);

    User login(User user);

    List<User> getUsers();
}
