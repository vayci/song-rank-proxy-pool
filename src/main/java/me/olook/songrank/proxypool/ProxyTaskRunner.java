package me.olook.songrank.proxypool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 */
@Component
public class ProxyTaskRunner implements ApplicationRunner {

    private final ScheduledPool scheduledPool;

    @Autowired
    public ProxyTaskRunner(ScheduledPool scheduledPool) {
        this.scheduledPool = scheduledPool;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scheduledPool.addPeriodSyncTask();
    }
}
