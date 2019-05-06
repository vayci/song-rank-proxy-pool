package me.olook.songrank.proxypool;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 */
@Component
public class ProxyTaskRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ScheduledPool.addPeriodSyncTask();
    }
}
