package me.olook.proxypool.core.impl;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.ProxyPoolProperties;
import me.olook.proxypool.core.ProxySink;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 */
@Slf4j
@Component
@EnableConfigurationProperties
public class RedisSink implements ProxySink {

    @Autowired
    private ProxyPoolProperties properties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void persistent(HttpHost httpHost) {
        log.info("accept proxy {}",httpHost);
        redisTemplate.opsForList().leftPush(properties.getKey(),httpHost);
    }
}
