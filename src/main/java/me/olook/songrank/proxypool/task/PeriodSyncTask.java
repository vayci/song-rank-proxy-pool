package me.olook.songrank.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.ScheduledPool;

/**
 * @author zhaohw
 * @date 2019-05-06 15:58
 */
@Slf4j
public class PeriodSyncTask implements Runnable{

    @Override
    public void run() {
        ScheduledPool.addProxySyncTask();
    }

}
