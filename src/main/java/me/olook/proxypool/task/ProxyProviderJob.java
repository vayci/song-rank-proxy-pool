package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.core.ProxySink;
import org.apache.http.HttpHost;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Red
 */
@Slf4j
public class ProxyProviderJob implements Runnable{

    private ProxyProvider proxyProvider;
    private ProxyChecker proxyChecker;
    private ProxySink proxySink;
    private Integer index;

    public ProxyProviderJob(Integer index,
                            ProxyProvider proxyProvider, ProxyChecker proxyChecker, ProxySink proxySink) {
        this.proxyProvider = proxyProvider;
        this.proxyChecker = proxyChecker;
        this.proxySink = proxySink;
        this.index = index;
    }

    @Override
    public void run() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
        String payload = proxyProvider.requestForPayload(index);
        List<HttpHost> httpHosts = proxyProvider.resolveProxy(payload);
        log.info("begin index: {} => {}", index, httpHosts.size());
        httpHosts.forEach(x->{
            CompletableFuture.runAsync(()->{
                boolean check = proxyChecker.check(x);
                if(check){
                    proxySink.persistent(x);
                    log.info("accept proxy {}", x);
                }
            });
        });
//        stopWatch.stop();
//        log.info("end index: {} => {}s", index, stopWatch.getTotalTimeSeconds());

    }
}
