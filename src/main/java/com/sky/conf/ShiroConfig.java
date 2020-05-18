package com.sky.conf;

import com.sky.auth.PermissionRealm;
import com.sky.common.entity.User;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
        SimpleCookie simpleCookie = new SimpleCookie();
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
        securityManager.setRealm(new PermissionRealm());
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

    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
    /**
     * 配置Realm
     */
   /* public AuthorizingRealm realm(){
        PermissionRealm permissionRealm = new PermissionRealm();

    }
*/




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
