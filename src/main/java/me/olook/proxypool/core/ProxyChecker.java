package me.olook.proxypool.core;

import org.apache.http.HttpHost;

public interface ProxyChecker {

    boolean check(HttpHost httpHost);
}
