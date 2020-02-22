package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.ProxyPoolProperties;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.core.ProxySink;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaohw
 * @date 2019-05-06 16:58
 */
@Slf4j
@Component
public class ProxyGetTask implements Runnable{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProxyPoolProperties properties;

    @Qualifier("gatherProxyProvider")
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

    @Override
    public void run() {
        log.info("now time is {} , next round will begin at {}",
                LocalDateTime.now(),LocalDateTime.now().plusSeconds(properties.getGather().getInterval()));
        if(needFill()){
            LocalDateTime end = LocalDateTime.now().plusSeconds(properties.getGather().getInterval());
            Integer index = properties.getGather().getStartPage();
            while (LocalDateTime.now().compareTo(end)<0){
                log.info("begin get data from page: {}",index);
                try{
                    String payload = provider.requestForPayload(index);
                    List<HttpHost> httpHosts = provider.resolveProxy(payload);
                    httpHosts.parallelStream()
                            .filter(host-> checker1.check(host))
                            //.filter(host-> checker2.check(host))
                    .forEach(httpHost -> sink.persistent(httpHost));
                    index++;
                }catch (Exception e){
                }
            }
           log.info("stop get data in page： {} , total {} pages",index,index-properties.getGather().getStartPage());
        }else{
            log.info("proxy pool has reached the threshold ");
        }

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
