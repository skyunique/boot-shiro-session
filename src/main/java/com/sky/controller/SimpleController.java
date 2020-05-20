package com.sky.controller;

import com.sky.common.entity.User;
import com.sky.common.response.BaseResponse;
import com.sky.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SimpleController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("index");
    }

    @RequestMapping("/register")
    public BaseResponse register(@RequestBody User user){
        userService.addUser(user);
        return new BaseResponse(0,"注册成功");
    }

    @RequestMapping("/home")
    public ModelAndView home(){
        ModelAndView home = new ModelAndView("home");
        home.addObject("users",userService.getUsers());
        return home;
    }

    @RequestMapping("/login")
    public BaseResponse<String> login(@RequestBody User user){
        BaseResponse<String> baseResponse = new BaseResponse<>(0,"登陆完成");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        subject.login(usernamePasswordToken);
        baseResponse.setData("/home");
        return baseResponse;

    }

}
