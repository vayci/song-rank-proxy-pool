package me.olook.songrank.proxypool;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.task.GatherProxyGetTask;
import me.olook.songrank.proxypool.task.GatherProxySyncTask;
import me.olook.songrank.proxypool.task.PeriodSyncTask;
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
    private GatherProxySyncTask proxySyncTask;

    @Autowired
    private GatherProxyGetTask gatherProxyGetTask;

    private static ScheduledFuture<?> proxySyncFuture;

    private static ScheduledFuture<?> proxyGetFuture;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void addPeriodSyncTask(){
        log.info("添加周期时间同步任务");
        scheduledExecutorService.scheduleAtFixedRate(periodSyncTask, 0, 6, TimeUnit.HOURS);
    }

    public void addProxySyncTask(){
        log.info("添加代理同步任务");
        proxySyncFuture = null;
        proxySyncFuture = scheduledExecutorService.
                scheduleAtFixedRate(proxySyncTask, 0, 2, TimeUnit.SECONDS);
    }

    public void stopProxySyncTask(){
        log.info("移除代理同步任务");
        if (proxySyncFuture != null) {
            proxySyncFuture.cancel(true);
        }
    }
    public void addProxyGetTask(){
        log.info("添加代理请求任务");
        proxyGetFuture = null;
        proxyGetFuture = scheduledExecutorService
                .scheduleAtFixedRate(gatherProxyGetTask, 0, 3, TimeUnit.MINUTES);
    }

    public void stopProxyGetTask(){
        log.info("移除代理请求任务");
        if (proxyGetFuture != null) {
            proxyGetFuture.cancel(true);
        }
    }
}
