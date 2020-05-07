package me.olook.proxypool.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.core.ProxySink;
import org.apache.http.HttpHost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 * @author zhaohw
 */
@Getter
@Slf4j
public class ProxyGetAction extends RecursiveAction {

    private Long id;

    private  Integer start;

    private  Integer end;

    private ProxyProvider proxyProvider;

    private ProxyChecker proxyChecker;

    private ProxySink proxySink;

    private RecursiveActionManager manager;

    public ProxyGetAction(Long id,Integer start , Integer end ,
                          ProxyProvider proxyProvider,ProxyChecker proxyChecker,ProxySink proxySink,RecursiveActionManager manager) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.proxyProvider = proxyProvider;
        this.proxyChecker = proxyChecker;
        this.proxySink = proxySink;
        this.manager = manager;
    }

    @Override
    protected void compute() {
        // 每个任务执行的最大页码数
        int max = 2;
        if(end - start <= max){
            LocalDateTime now = LocalDateTime.now();
            log.debug("执行 => {} {}",start,end);
            for(Integer i = start; i <= end; i++){
                String payload = proxyProvider.requestForPayload(i);
                List<HttpHost> httpHosts = proxyProvider.resolveProxy(payload);
                httpHosts.parallelStream().filter(host->proxyChecker.check(host))
                        .forEach(host->proxySink.persistent(host));
            }
            log.debug("结束 => {} {} [开始时间: {}]",start,end,now.format(DateTimeFormatter.ISO_DATE_TIME));
        }else{
            Integer middle = (start + end) / 2;
            ProxyGetAction leftTask = new ProxyGetAction(id,start,middle,proxyProvider,proxyChecker,proxySink,manager);
            ProxyGetAction rightTask = new ProxyGetAction(id,middle+1,end,proxyProvider,proxyChecker,proxySink,manager);
            manager.addTask(leftTask);
            manager.addTask(rightTask);
            leftTask.fork();
            rightTask.fork();
        }
    }
}
