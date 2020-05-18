package com.sky.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/redis.properties")
@Getter
@Setter
public class RedisConfig {

    @Value("${shiro.redis.host}")
    private String host;
    @Value("${shiro.redis.timeout}")
    private String timeout;
}
