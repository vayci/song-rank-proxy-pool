package me.olook.proxypool.task;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.config.ProxyPoolProperties;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.core.ProxySink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author Red
 */
@Slf4j
@Component
public class ProxySchedule {

    private ExecutorService executorService = new ThreadPoolExecutor(10, 20,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),new DefaultThreadFactory("proxy"));

    public static ExecutorService checkerService = new ThreadPoolExecutor(70, 70,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(70),new DefaultThreadFactory("checker"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

//    public static ExecutorService checkerService = new ForkJoinPool(24);

    @Autowired
    private ProxyProvider proxyProvider;
    @Qualifier("playRecordChecker")
    @Autowired
    private ProxyChecker proxyChecker;
    @Autowired
    private ProxySink proxySink;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProxyPoolProperties properties;

    @Scheduled(fixedRate = 40 * 1000)
    public void run(){
        Long size = redisTemplate.opsForList().size(properties.getKey());
        log.info("-------------------------------------------");
        log.info("{}",checkerService);
        log.info("active pool size: {}", size);
        log.info("-------------------------------------------");
        for (int i = 1; i < 11; i++) {
            ProxyProviderJob job = new ProxyProviderJob(i, proxyProvider, proxyChecker, proxySink);
            executorService.submit(job);
        }
    }
}
