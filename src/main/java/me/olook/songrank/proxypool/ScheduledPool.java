package me.olook.songrank.proxypool;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.task.GatherProxyGetTask;
import me.olook.songrank.proxypool.task.GatherProxySyncTask;
import me.olook.songrank.proxypool.task.PeriodSyncTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaohw
 * @date 2019-05-06 15:47
 */
@Slf4j
public class ScheduledPool {

    private static ScheduledFuture<?> proxySyncFuture;

    private static ScheduledFuture<?> proxyGetFuture;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static void addPeriodSyncTask(){
        log.info("添加周期时间同步任务");
        scheduledExecutorService.scheduleAtFixedRate(new PeriodSyncTask(), 0, 6, TimeUnit.HOURS);
    }

    public static void addProxySyncTask(){
        log.info("添加代理同步任务");
        proxySyncFuture = null;
        proxySyncFuture = scheduledExecutorService.
                scheduleAtFixedRate(new GatherProxySyncTask(), 0, 2, TimeUnit.SECONDS);
    }

    public static void stopProxySyncTask(){
        log.info("移除代理同步任务");
        if (proxySyncFuture != null) {
            proxySyncFuture.cancel(true);
        }
    }
    public static void addProxyGetTask(){
        log.info("添加代理请求任务");
        proxyGetFuture = null;
        proxyGetFuture = scheduledExecutorService
                .scheduleAtFixedRate(new GatherProxyGetTask(), 0, 3, TimeUnit.MINUTES);
    }

    public static void stopProxyGetTask(){
        log.info("移除代理请求任务");
        if (proxyGetFuture != null) {
            proxyGetFuture.cancel(true);
        }
    }
}
