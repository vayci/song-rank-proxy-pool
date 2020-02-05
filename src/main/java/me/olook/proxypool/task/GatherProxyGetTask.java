package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.ProxyPoolProperties;
import me.olook.proxypool.provider.impl.ProxyListOrgProvider;
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

    @Autowired
    private ProxyPoolProperties properties;

    @Autowired
    private ProxyListOrgProvider provider;

    @Override
    public void run() {
        log.info("now time is {} , next round will begin at {}",
                LocalDateTime.now(),LocalDateTime.now().plusSeconds(properties.getGather().getInterval()));
        if(provider.needFill()){
            LocalDateTime end = LocalDateTime.now().plusMinutes(1);
            Integer index = properties.getGather().getStartPage();
            while (LocalDateTime.now().compareTo(end)<0){
                log.info("begin get data from page: {}",index);
                try{
                    String payload = provider.requestForPayload(index);
                    List<HttpHost> httpHosts = provider.resolveProxy(payload);
                    httpHosts.parallelStream().forEach(host->{
                        provider.checkAndSaveProxy(host);
                    });
                    index++;
                }catch (Exception e){
                }
            }
           log.info("stop get data in page： {} , total {} pages",index,index-properties.getGather().getStartPage());
        }else{
            log.info("proxy pool has reached the threshold ");
        }

    }

}
