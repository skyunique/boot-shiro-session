package com.sky.service.impl;

import com.sky.common.entity.User;
import com.sky.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String, User> redisTemplate;



    @Override
    public void addUser(User user) {

    }

    @Override
    public User login(User user) {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }
}
