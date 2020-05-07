package me.olook.proxypool.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaohw
 */
@Slf4j
@Component
public class RecursiveActionManager {

    private List<ProxyGetAction> tasks;

    public RecursiveActionManager() {
        tasks = new ArrayList<>();
    }

    public void addTask(ProxyGetAction task) {
        tasks.add(task);
    }

    public void cancelTask() {
        List<ProxyGetAction> collect = tasks.stream()
                .filter(t -> System.currentTimeMillis() - t.getId() > 20000)
                .collect(Collectors.toList());
        collect.forEach(t->{
            t.cancel(true);
            tasks.remove(t);
        });
    }

    public int size() {
        return tasks.size();
    }
}
