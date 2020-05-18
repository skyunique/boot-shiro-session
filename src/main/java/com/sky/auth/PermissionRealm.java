package com.sky.auth;


import com.sky.common.entity.User;
import com.sky.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;


public class PermissionRealm  extends AuthorizingRealm {

    @Autowired
    private UserService userService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Object principal = principalCollection.getPrimaryPrincipal();
        User user = (User) principal;
        Set<String> roles = new HashSet<>();
        roles.add("user");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {


        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //从token获取用户名和密码
        String username = token.getUsername();
        String  password = String.valueOf(token.getPassword());

        User user = userService.login(new User(username, password));
        if(user == null){
            throw  new AuthenticationException("账户或密码错误");
        }
        Object p = user;
        Object pwd = user.getPassword();
        ByteSource bytes = ByteSource.Util.bytes(user.getUsername());
        String name = getName();
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(p,password,bytes,name);
        return  simpleAuthenticationInfo;

    }
}
