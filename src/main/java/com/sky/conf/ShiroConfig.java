package com.sky.conf;

import com.sky.auth.PermissionRealm;
import com.sky.common.entity.User;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashMap;

@Configuration
public class ShiroConfig {

    @Bean
    public RedisConfig redisConfig(){
        return  new RedisConfig();
    }

    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisConfig().getHost());
        redisManager.setTimeout(Integer.parseInt(redisConfig().getTimeout()));
        return  redisManager;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator(){
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator()); //sessionId生成器
        return redisSessionDAO;
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("SHAREJSESSIONID");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setPath("/");
        return simpleCookie;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(simpleCookie());  //设置sessionId
        sessionManager.setSessionDAO(redisSessionDAO());   //设置sessionDao
        sessionManager.setGlobalSessionTimeout(Long.parseLong(redisConfig().getTimeout())); //设置session超时时间
        sessionManager.setDeleteInvalidSessions(true);    //删除无效session
        return  sessionManager;
    }


    /**
     * 1. 配置SecurityManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setSessionManager(sessionManager());
        // 配置缓存的话，退出登录的时候crazycake会报错，要求放在session里面的实体类必须有个id标识
        //securityManager.setCacheManager(cacheManager());
        return securityManager;
    }


    /**
     * 2. 配置缓存
     * @return
     */
//    @Bean
//    public CacheManager cacheManager(){
//        EhCacheManager ehCacheManager = new EhCacheManager();
//        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
//        return ehCacheManager;
//    }

    @Bean
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
    /**
     * 3.配置Realm
     */
    @Bean
    public AuthorizingRealm realm(){
        PermissionRealm permissionRealm = new PermissionRealm();
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //指定加密算法
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        //指定加密次数
        hashedCredentialsMatcher.setHashIterations(10);
        //指定这个就不会报错
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        permissionRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return permissionRealm;
    }


    /**
     * 4.配置LifecycleBeanPostProcessor,可以用来调用Spring IOC容器中 Shiro Bean的生命周期方法
     * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 5.开启shiro的注解,但是必须配置第四步
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator (){
        return  new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        //静态资源
        map.put("/css/**","anon");
        map.put("/js/**","anon");

        //公共路径
        map.put("/login","anon");
        map.put("/register","anon");
        //map.put("/*","anon");

        //登出,项目中没有logout路径，因为shiro是过滤器，而springMvc是Servlet,shiro回先执行
        map.put("logout","logout");

        //授权
        map.put("/user/**","authc,roles[user]");
        map.put("/admin/**","authc,roles[admin]");

        //everything else requires authentication
        map.put("/**","authc");

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //配置SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        //配置权限路径
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        //配置登录Url
        shiroFilterFactoryBean.setLoginUrl("/");
        //配置无权限路径
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        return shiroFilterFactoryBean;
    }




    /**
     * 配置RedisTemplate，充当数据库服务
     * @return
     */
    @Bean
    public RedisTemplate<String, User> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,User> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        return redisTemplate;
    }
}
