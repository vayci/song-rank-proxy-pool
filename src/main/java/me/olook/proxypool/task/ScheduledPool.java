package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaohw
 * @date 2019-05-06 15:47
 */
@Slf4j
@Component
public class ScheduledPool {

    @Autowired
    private PeriodSyncTask periodSyncTask;

    @Autowired
    private ProxySyncTask proxySyncTask;

    @Autowired
    private ProxyGetTask proxyGetTask;

    private static ScheduledFuture<?> proxySyncFuture;

    private static ScheduledFuture<?> proxyGetFuture;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void addPeriodSyncTask(){
        log.info("[START] add period sync task");
        scheduledExecutorService.scheduleAtFixedRate(periodSyncTask, 0, 6, TimeUnit.HOURS);
    }

    public void addProxySyncTask(){
        log.info("[START] add proxy sync task");
        proxySyncFuture = null;
        proxySyncFuture = scheduledExecutorService.
                scheduleAtFixedRate(proxySyncTask, 3, 3, TimeUnit.SECONDS);
    }

    public void stopProxySyncTask(){
        log.info("[STOP] stop proxy sync task");
        if (proxySyncFuture != null) {
            proxySyncFuture.cancel(true);
        }
    }
    public void addProxyGetTask(Integer period){
        log.info("[START] add proxy get task");
        proxyGetFuture = null;
        proxyGetFuture = scheduledExecutorService
                .scheduleAtFixedRate(proxyGetTask, 0, period, TimeUnit.SECONDS);
    }

    public void stopProxyGetTask(){
        log.info("[STOP] stop proxy get task");
        if (proxyGetFuture != null) {
            proxyGetFuture.cancel(true);
        }
    }
}
