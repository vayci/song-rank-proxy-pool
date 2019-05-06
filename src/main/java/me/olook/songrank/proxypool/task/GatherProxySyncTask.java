package me.olook.songrank.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.ScheduledPool;
import me.olook.songrank.proxypool.provider.impl.GatherProxyProvider;
import org.apache.http.HttpHost;

import java.util.List;

/**
 * @author zhaohw
 * @date 2019-05-06 16:25
 */
@Slf4j
public class GatherProxySyncTask implements Runnable{

    private GatherProxyProvider provider = new GatherProxyProvider();

    private HttpHost firstHost;

    @Override
    public void run() {
        String payload = provider.requestForPayload(1);
        if(payload != null) {
            List<HttpHost> httpHosts = provider.resolveProxy(payload);
            if(firstHost == null){ firstHost = httpHosts.get(0); }
            else if(!firstHost.equals(httpHosts.get(0))){
                log.info("数据变更,同步完成");
                ScheduledPool.stopProxySyncTask();
                ScheduledPool.addProxyGetTask();
            }
        }
    }
}
