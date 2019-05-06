package me.olook.songrank.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.ScheduledPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 * @date 2019-05-06 15:58
 */
@Slf4j
@Component
public class PeriodSyncTask implements Runnable{

    @Autowired
    private ScheduledPool scheduledPool;

    @Override
    public void run() {
        scheduledPool.addProxySyncTask();
    }

}
