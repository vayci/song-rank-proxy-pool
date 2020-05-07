package me.olook.proxypool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author zhaohw
 */
@Configuration
public class RedisConfiguration {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Bean
//    public RedisTemplate redisTemplateInit() {
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
}

