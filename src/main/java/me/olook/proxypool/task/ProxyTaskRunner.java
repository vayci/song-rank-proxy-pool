package me.olook.proxypool.task;

import me.olook.proxypool.config.ProxyPoolProperties;
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
    private ProxyPoolProperties properties;

    @Autowired
    public ProxyTaskRunner(ScheduledPool scheduledPool) {
        this.scheduledPool = scheduledPool;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(properties.getSync().isPeriodSync()){
            scheduledPool.addPeriodSyncTask();
        }
        else if(properties.getSync().isProxySync()){
            scheduledPool.addProxySyncTask();
        }
        else {
            scheduledPool.addProxyGetTask(properties.getGather().getInterval());
        }
    }
}
