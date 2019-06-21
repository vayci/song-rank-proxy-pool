package me.olook.songrank.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.provider.impl.GatherProxyProvider;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaohw
 * @date 2019-05-06 16:58
 */
@Slf4j
@Component
public class GatherProxyGetTask implements Runnable{

    private static int indexBegin = 1;

    @Autowired
    private GatherProxyProvider provider;

    @Override
    public void run() {
        if(provider.needFill()){
            log.info("代理抓取开始,{}页开始",indexBegin);
            LocalDateTime end = LocalDateTime.now().plusMinutes(2).plusSeconds(15);
            Integer index = indexBegin;
            while (LocalDateTime.now().compareTo(end)<0){
                String payload = provider.requestForPayload(index);
                List<HttpHost> httpHosts = provider.resolveProxy(payload);
                httpHosts.parallelStream().forEach(host->{
                    provider.checkAndSaveProxy(host);
                });
                index++;
            }
            log.info("本轮抓取结束，{}页结束，共抓取{}页数据",index,index-indexBegin);
        }else{
            log.info("代理池状态良好，无需补充");
        }

    }

    public static void setIndexBegin(int indexBegin) {
        log.info("设置代理抓取起始页为 {}",indexBegin);
        GatherProxyGetTask.indexBegin = indexBegin;
    }
}
