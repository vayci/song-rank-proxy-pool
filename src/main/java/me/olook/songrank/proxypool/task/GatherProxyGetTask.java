package me.olook.songrank.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.provider.impl.GatherProxyProvider;
import org.apache.http.HttpHost;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaohw
 * @date 2019-05-06 16:58
 */
@Slf4j
public class GatherProxyGetTask implements Runnable{

    private GatherProxyProvider provider = new GatherProxyProvider();

    @Override
    public void run() {
        log.info("代理抓取开始");
        LocalDateTime end = LocalDateTime.now().plusMinutes(2).plusSeconds(15);
        Integer index = 1;
        while (LocalDateTime.now().compareTo(end)<0){
            String payload = provider.requestForPayload(index);
            List<HttpHost> httpHosts = provider.resolveProxy(payload);
            httpHosts.parallelStream().forEach(host->{
                provider.checkAndSaveProxy(host);
            });
            index++;
        }
        log.info("本轮抓取结束，共获取{}页数据",index-1);
    }
}
