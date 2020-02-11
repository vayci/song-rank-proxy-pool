package me.olook.proxypool.provider;

import org.apache.http.HttpHost;

public interface ProxyChecker {

    boolean check(HttpHost httpHost);
}
