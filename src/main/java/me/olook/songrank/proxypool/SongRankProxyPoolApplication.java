package me.olook.songrank.proxypool;

import me.olook.songrank.proxypool.provider.BaseVpnProxyProvider;
import me.olook.songrank.proxypool.task.GatherProxyGetTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class SongRankProxyPoolApplication {

    public static void main(String[] args) {
        if(args.length>0){ GatherProxyGetTask.setIndexBegin(Integer.parseInt(args[0])); }
        if(args.length>1){ BaseVpnProxyProvider.setVpnPort(Integer.parseInt(args[1])); }
        SpringApplication.run(SongRankProxyPoolApplication.class, args);
    }

}
