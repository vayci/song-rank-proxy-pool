package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.config.ProxyPoolProperties;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.core.ProxySink;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;

/**
 * @author zhaohw
 * @date 2019-05-06 16:58
 */
@Slf4j
@Component
public class ProxyGetTask implements Runnable{

    ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProxyPoolProperties properties;

    @Qualifier("proxyListOrgProvider")
    @Autowired
    private ProxyProvider provider;

    @Qualifier("playRecordChecker")
    @Autowired
    private ProxyChecker checker1;

    @Qualifier("commentChecker")
    @Autowired
    private ProxyChecker checker2;

    @Qualifier("redisSink")
    @Autowired
    private ProxySink sink;

    @Autowired
    private RecursiveActionManager actionManager;

    @Autowired
    private PoolingHttpClientConnectionManager connectionManager;

    @Override
    public void run() {
        log.info("now time is {} , next round will begin at {}",
                LocalDateTime.now(),LocalDateTime.now().plusSeconds(properties.getGather().getInterval()));
        connectionManager.closeExpiredConnections();
        //actionManager.cancelTask();
        forkJoinPool.shutdownNow();
        forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(new ProxyGetAction(System.currentTimeMillis(),1,10,provider,checker1,sink,actionManager));
    }

    private boolean needFill(){
        if(properties.getLimit()<=0){
            return true;
        }
        Long size = redisTemplate.opsForList().size(properties.getKey());
        log.info("active proxy pool size: {} , threshold: {}",size,properties.getLimit());
        return size <= properties.getLimit();

    }
}
