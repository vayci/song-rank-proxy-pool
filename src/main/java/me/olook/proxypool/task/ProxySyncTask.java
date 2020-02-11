package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.ProxyPoolProperties;
import me.olook.proxypool.provider.impl.GatherProxyProvider;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhaohw
 * @date 2019-05-06 16:25
 */
@Slf4j
@Component
public class ProxySyncTask implements Runnable{

    @Autowired
    private ScheduledPool scheduledPool;

    @Autowired
    private GatherProxyProvider provider;

    @Autowired
    private ProxyPoolProperties properties;

    private HttpHost firstHost;

    @Override
    public void run() {
        String payload = provider.requestForPayload(1);
        if(payload != null) {
            List<HttpHost> httpHosts = provider.resolveProxy(payload);
            if(firstHost == null){ firstHost = httpHosts.get(0); }
            else if(!firstHost.equals(httpHosts.get(0))){
                log.info("data change detected , sync ok");
                scheduledPool.stopProxySyncTask();
                scheduledPool.stopProxyGetTask();
                scheduledPool.addProxyGetTask(properties.getGather().getInterval());
            }
        }
    }
}
